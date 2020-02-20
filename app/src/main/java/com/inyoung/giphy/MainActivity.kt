package com.inyoung.giphy

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.inyoung.giphy.Constants.API_KEY
import com.inyoung.giphy.model.SearchResponse
import com.inyoung.giphy.network.ApiManager
import com.inyoung.giphy.view.SearchImageAdapter
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.DisplayMetrics

class MainActivity : Activity() {
    private val SPAN_COUNT = 2
    private val IMAGE_LIMIT = 50
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setView()
        search("car")
    }

    private fun search(query: String) {
        ApiManager.getSearchService().checkAppVersion(
            API_KEY,
            query,
            IMAGE_LIMIT,
            0,
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
                        (recyclerView.adapter as SearchImageAdapter).setImages(it.images)
                    }
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {

            }
        })
    }

    private fun setView() {
        recycler_view.apply {
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)

            adapter = SearchImageAdapter(
                listOf(),
                metrics,
                SPAN_COUNT
            )
            layoutManager = StaggeredGridLayoutManager(
                SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
        }
    }
}