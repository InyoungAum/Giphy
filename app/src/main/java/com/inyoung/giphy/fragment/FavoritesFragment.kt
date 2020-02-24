package com.inyoung.giphy.fragment

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_favorites.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesFragment : Fragment() {
    companion object {
        private const val SPAN_COUNT = 2
        private const val IMAGE_OFFSET_COUNT = 15
    }

    private var currentOffset = 0
    private var query: String = ""
    private lateinit var realm: Realm
    private lateinit var likeImages: List<LikeImage>

    private val recyclerView by lazy { recycler_view }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_favorites, null)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        realm = Realm.getDefaultInstance()
        loadFavoriteImages()
        setView()
        getImages(generateImageIds())
    }

    private fun loadFavoriteImages() {
        realm.let {
            likeImages = it.copyFromRealm(
                it.where<LikeImage>().findAll()
            )
        }
    }

    private fun generateImageIds(): String? {
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

    fun getImages(ids: String?) {
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
                                    addImages(it.images)
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
            val metrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(metrics)

            adapter = ImageAdapter(mutableListOf(), metrics, SPAN_COUNT,
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
                override fun onLoad() {
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
                    loadmore(dy)
                }
            })
        }
    }


}