package com.inyoung.giphy.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.inyoung.giphy.Constants.KEY_IMAGE_ID
import com.inyoung.giphy.R
import com.inyoung.giphy.activity.DetailGifActivity
import com.inyoung.giphy.hideActiveKeyboard
import com.inyoung.giphy.model.ImageListResponse
import com.inyoung.giphy.network.ApiManager
import com.inyoung.giphy.view.ImageAdapter
import com.inyoung.giphy.view.LoadmoreRecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {
    companion object {
        private const val SPAN_COUNT = 2
        private const val IMAGE_OFFSET_COUNT = 15
    }
    private var currentOffset = 0
    private var query: String = ""

    private lateinit var recyclerView: LoadmoreRecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageView
    private lateinit var emptyView: TextView
    private lateinit var titleText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        setView(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        if (!TextUtils.isEmpty(query)) {
            recyclerView.load(needRefresh = true)
            currentOffset = 0
        } else {
            titleText.text = "Search"
            currentOffset = 0
        }
    }

    private fun search(query: String, reload: Boolean = false) {
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
                            addImages(it.images, reload)
                        }
                        titleText.text = query
                    }
                }
                recyclerView.loadFinish(true)
            }

            override fun onFailure(call: Call<ImageListResponse>, t: Throwable) {
                recyclerView.loadFinish(false)
            }
        })
    }

    private fun setView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view)
        searchEditText = view.findViewById(R.id.edit_search)
        searchButton = view.findViewById(R.id.button_search)
        emptyView = view.findViewById(R.id.empty_view)
        titleText = view.findViewById(R.id.text_title)

        recyclerView.apply {
            adapter = ImageAdapter(mutableListOf(), resources.displayMetrics, SPAN_COUNT,
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

            setOnLoadListener(object: LoadmoreRecyclerView.OnLoadListener{
                override fun onLoad(needRefresh: Boolean) {
                    (adapter as ImageAdapter).prepareLoadImage()
                    changeEmptyView()
                    search(query, needRefresh)
                    currentOffset += IMAGE_OFFSET_COUNT
                    stopScroll()
                }

                override fun onFinish(isSuccess: Boolean) {
                    if (!isSuccess) {
                        currentOffset -= IMAGE_OFFSET_COUNT
                    }
                }
            })

            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    load(dy)
                }
            })

            setEmptyView(emptyView)
        }

        searchButton.setOnClickListener {
            searchButtonClick()
        }

        searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (v.id == R.id.edit_search) {
                when(actionId) {
                    EditorInfo.IME_ACTION_SEARCH -> {
                        searchButtonClick()
                        true
                    }
                    else -> false
                }

            } else true
        }
    }

    private fun searchButtonClick() {
        val currentQuery = searchEditText.text.toString()
        if (currentQuery != query || currentOffset != IMAGE_OFFSET_COUNT) {
            if (!TextUtils.isEmpty(currentQuery)) {
                val needRefresh = query != currentQuery
                query = currentQuery
                recyclerView.load(needRefresh = needRefresh)
                (activity as AppCompatActivity).hideActiveKeyboard()
            }
        }
    }

    private fun changeEmptyView() {
        if (TextUtils.isEmpty(query)) {
            emptyView.text = "새로운 이미지를 찾아볼까요?"
            emptyView.setCompoundDrawablesWithIntrinsicBounds(null,
                resources.getDrawable(R.drawable.ic_search),
                null ,
                null
            )
        } else {
            emptyView.text ="검색결과가 없어요"
            emptyView.setCompoundDrawablesWithIntrinsicBounds(null,
                resources.getDrawable(R.drawable.ic_no_result),
                null ,
                null
            )
        }
    }
}