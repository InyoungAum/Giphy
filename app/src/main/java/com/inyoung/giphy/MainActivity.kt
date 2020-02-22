package com.inyoung.giphy

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.inyoung.giphy.Constants.API_KEY
import com.inyoung.giphy.model.SearchResponse
import com.inyoung.giphy.network.ApiManager
import com.inyoung.giphy.view.SearchImageAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.DisplayMetrics
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.ContentLoadingProgressBar
import com.inyoung.giphy.model.GifImage

class MainActivity : Activity() {
    companion object {
        private const val SPAN_COUNT = 2
        private const val IMAGE_OFFSET_COUNT = 15
    }
    private var currentOffset = 0
    private var query: String = ""
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }
    private val searchEditText by lazy { findViewById<EditText>(R.id.edit_search) }
    private val searchButton by lazy { findViewById<ImageView>(R.id.button_search) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setView()
    }

    private fun search(query: String) {
        ApiManager.getSearchService().checkAppVersion(
            API_KEY,
            query,
            IMAGE_OFFSET_COUNT,
            currentOffset,
            "",
            "ko",
            "ran"
        ).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        (recyclerView.adapter as SearchImageAdapter).apply {
                            addImages(it.images)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {

            }
        })
    }

    private fun setView() {
        recyclerView.apply {
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)

            adapter = SearchImageAdapter(mutableListOf(), metrics, SPAN_COUNT)
            layoutManager = StaggeredGridLayoutManager(
                SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisible = layoutManager.findLastCompletelyVisibleItemPositions(null).last()

                    if (lastVisible >= totalItemCount - 1) {
                        currentOffset += IMAGE_OFFSET_COUNT
                        search(query)
                        stopScroll()
                    }
                }
            })
        }

        searchButton.setOnClickListener {
            val currentQuery = searchEditText.text.toString()
            if (currentQuery != query || currentOffset != IMAGE_OFFSET_COUNT) {
                if (!TextUtils.isEmpty(currentQuery)) {
                    query = currentQuery
                    search(query)
                }
            }
        }
    }
}