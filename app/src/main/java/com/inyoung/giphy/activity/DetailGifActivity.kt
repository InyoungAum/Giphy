package com.inyoung.giphy.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.inyoung.giphy.Constants
import com.inyoung.giphy.R
import com.inyoung.giphy.model.ImageResponse
import com.inyoung.giphy.model.LikeImage
import com.inyoung.giphy.network.ApiManager
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_detail_gif.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailGifActivity : AppCompatActivity() {
    private var likeImage: LikeImage? = null
    private val imageView by lazy { image_detail }
    private val titleText by lazy { text_title }
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_gif)
        realm = Realm.getDefaultInstance()

        val imageId = intent.getStringExtra(Constants.KEY_IMAGE_ID)
        getImage(imageId)
        findLikeImage(imageId)
        button_back.setOnClickListener { finish() }
        button_like.setOnClickListener { changeImageLike(imageId) }
    }

    private fun findLikeImage(id: String) {
        val ret = realm.where<LikeImage>().equalTo("id", id).findFirst()
        likeImage = ret?.let { realm.copyFromRealm(ret) }
        changeLikeButton()
    }

    private fun changeImageLike(id: String) {
        realm.executeTransactionAsync(Realm.Transaction {
            if (likeImage == null) {
                val ret = it.createObject(LikeImage::class.java, id)
                likeImage = it.copyFromRealm(ret)
            } else {
                likeImage!!.like = !likeImage!!.like
                it.copyToRealmOrUpdate(likeImage)
            }
        }, Realm.Transaction.OnSuccess {
            changeLikeButton()
        })
    }

    private fun changeLikeButton() {
        likeImage?.let {
            button_like.setImageResource(
                if (it.like) R.drawable.ic_dislike
                else R.drawable.ic_like
            )
        }
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
}

