package com.inyoung.giphy.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.inyoung.giphy.Constants
import com.inyoung.giphy.R
import com.inyoung.giphy.model.ImageResponse
import com.inyoung.giphy.model.LikeImage
import com.inyoung.giphy.network.ApiManager
import com.inyoung.giphy.viewmodel.FavoritesViewModel
import kotlinx.android.synthetic.main.activity_detail_gif.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent

class DetailGifActivity : AppCompatActivity() {
    private val imageView by lazy { image_detail }
    private val titleText by lazy { text_title }

    private lateinit var favoritesViewModel: FavoritesViewModel
    private var likeImage: LikeImage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_gif)

        val imageId = intent.getStringExtra(Constants.KEY_IMAGE_ID)

        favoritesViewModel = ViewModelProviders
            .of(this)
            .get(FavoritesViewModel::class.java)

        likeImage = favoritesViewModel.findLikeImage(imageId)?.let{
            changeLikeButton(it.like)
            it
        }

        getImage(imageId)

        button_back.setOnClickListener { finish() }
        button_like.setOnClickListener { likeImage(imageId) }
    }

    private fun likeImage(id: String) {
        if (likeImage != null) {
            favoritesViewModel.changeLikeState(likeImage!!)
        } else {
            favoritesViewModel.addImage(id)
            likeImage = favoritesViewModel.findLikeImage(id)
        }
        changeLikeButton(likeImage!!.like)
    }

    private fun changeLikeButton(isLike: Boolean) {
        button_like.setImageResource(
            if (isLike) R.drawable.ic_dislike
            else R.drawable.ic_like
        )
    }

    private fun getImage(id: String) {
        ApiManager.getImageService().getSingleImage(id)
            .enqueue(object : Callback<ImageResponse> {
                override fun onResponse(
                    call: Call<ImageResponse>,
                    response: Response<ImageResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            titleText.text = it.image.title
                            Glide.with(this@DetailGifActivity)
                                .load(it.image.images?.fixedWidth?.url)
                                .into(imageView)
                        }
                    }
                }

                override fun onFailure(call: Call<ImageResponse>, t: Throwable) {

                }
        })
    }

    override fun finish() {
        val intent = Intent().apply {
            putExtra(Constants.KEY_IMAGE_ID, likeImage!!.id)
        }
        setResult(
            if (likeImage!!.like) Constants.RESULT_LIKE
                else Constants.RESULT_DISLIKE,
            intent
        )
        super.finish()
    }
}

