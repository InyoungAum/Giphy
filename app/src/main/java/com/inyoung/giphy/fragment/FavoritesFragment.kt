package com.inyoung.giphy.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.inyoung.giphy.Constants
import com.inyoung.giphy.R
import com.inyoung.giphy.activity.DetailGifActivity
import com.inyoung.giphy.model.ImageListResponse
import com.inyoung.giphy.model.LikeImage
import com.inyoung.giphy.network.ApiManager
import com.inyoung.giphy.view.ImageAdapter
import com.inyoung.giphy.view.LoadmoreRecyclerView
import com.inyoung.giphy.viewmodel.FavoritesViewModel
import io.realm.Realm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesFragment : Fragment() {
    companion object {
        private const val SPAN_COUNT = 2
        private const val IMAGE_OFFSET_COUNT = 15
    }

    private lateinit var favoritesViewModel: FavoritesViewModel

    private var currentOffset = 0
    private var query: String = ""
    private lateinit var realm: Realm

    private lateinit var recyclerView: LoadmoreRecyclerView
    private lateinit var emptyView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favoritesViewModel = ViewModelProviders.of(this)
            .get(FavoritesViewModel::class.java)

        favoritesViewModel.likeImages.observe(this, Observer {
            getImages(generateImageIds(it), true)
            recyclerView.changeEmptyViewVisibility()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        emptyView = view.findViewById(R.id.empty_view)
        realm = Realm.getDefaultInstance()
        setView()
        return view
    }

    private fun generateImageIds(likeImages: List<LikeImage>): String? {
        return if (likeImages.size > currentOffset) {
            val end = if (likeImages.size < currentOffset + IMAGE_OFFSET_COUNT) likeImages.size
                else currentOffset + IMAGE_OFFSET_COUNT
            val query = StringBuilder()
            likeImages.subList(currentOffset, end).forEach {
                query.append(it.id)
                query.append(", ")
            }
            query.toString()
        } else null
    }

    private fun getImages(ids: String?, reload: Boolean = false) {
        ids?.let {
            ApiManager.getImageService().getImagesById(it)
                .enqueue(object : Callback<ImageListResponse> {
                    override fun onResponse(
                        call: Call<ImageListResponse>,
                        response: Response<ImageListResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                (recyclerView.adapter as ImageAdapter).apply {
                                    addImages(it.images, reload)
                                }
                            }
                        }
                        recyclerView.loadFinish(true)
                    }

                    override fun onFailure(call: Call<ImageListResponse>, t: Throwable) {
                        recyclerView.loadFinish(false)
                    }
                })
        }
    }

    private fun setView() {
        recyclerView.apply {
            adapter = ImageAdapter(mutableListOf(), resources.displayMetrics, SPAN_COUNT,
                object : ImageAdapter.OnItemClickListener {
                    override fun onItemClick(id: String) {
                        startActivity(
                            Intent(activity, DetailGifActivity::class.java).apply {
                                putExtra(Constants.KEY_IMAGE_ID, id)
                            }
                        )
                    }
                })

            layoutManager = StaggeredGridLayoutManager(
                SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL
            )
            setOnLoadListener(object : LoadmoreRecyclerView.OnLoadListener{
                override fun onLoad(needRefresh: Boolean) {
                    currentOffset += IMAGE_OFFSET_COUNT
                    getImages(query)
                    stopScroll()
                }

                override fun onFinish(isSuccess: Boolean) {
                    if (!isSuccess) {
                        currentOffset -= IMAGE_OFFSET_COUNT
                    }
                }
            })

            setHasFixedSize(true)

            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    load(dy)
                }
            })

            setEmptyView(emptyView)
        }
    }
}
