package com.inyoung.giphy.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.inyoung.giphy.Constants
import com.inyoung.giphy.Constants.KEY_IMAGE_ID
import com.inyoung.giphy.R
import com.inyoung.giphy.activity.DetailGifActivity
import com.inyoung.giphy.model.ImageListResponse
import com.inyoung.giphy.network.ApiManager
import com.inyoung.giphy.view.ImageAdapter
import kotlinx.android.synthetic.main.fragment_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {
    companion object {
        private const val SPAN_COUNT = 2
        private const val IMAGE_OFFSET_COUNT = 15
        private var load = false
    }
    private var currentOffset = 0
    private var query: String = ""

    private val recyclerView by lazy { recycler_view }
    private val searchEditText by lazy { edit_search }
    private val searchButton by lazy { button_search }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_search, null)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setView()
    }

    private fun search(query: String) {
        ApiManager.getImageService().getImagesByQuery(
            query,
            IMAGE_OFFSET_COUNT,
            currentOffset
        ).enqueue(object : Callback<ImageListResponse> {
            override fun onResponse(
                call: Call<ImageListResponse>,
                response: Response<ImageListResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        (recyclerView.adapter as ImageAdapter).apply {
                            addImages(it.images)
                        }
                    }
                }
                load = false
            }

            override fun onFailure(call: Call<ImageListResponse>, t: Throwable) {
                currentOffset -= IMAGE_OFFSET_COUNT
                load = false
            }
        })
    }

    private fun setView() {
        recyclerView.apply {
            val metrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(metrics)

            adapter = ImageAdapter(mutableListOf(), metrics, SPAN_COUNT,
                object : ImageAdapter.OnItemClickListener {
                    override fun onItemClick(id: String) {
                        startActivity(
                            Intent(activity, DetailGifActivity::class.java).apply {
                                putExtra(KEY_IMAGE_ID, id)
                            }
                        )
                    }
                })

            layoutManager = StaggeredGridLayoutManager(
                SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisible = layoutManager.findLastCompletelyVisibleItemPositions(null)

                    if (lastVisible.contains(totalItemCount - 1) && dy > 0 && !load) {
                        load = true
                        (adapter as ImageAdapter).loadImage()
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