package com.inyoung.giphy.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.inyoung.giphy.Constants
import com.inyoung.giphy.R
import com.inyoung.giphy.model.DetailResponse
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
        findLike(imageId)
        button_back.setOnClickListener { finish() }
        button_like.setOnClickListener { like(imageId) }
    }

    private fun findLike(id: String) {
        val ret = realm.where<LikeImage>().equalTo("id", id).findFirst()
        likeImage = ret?.let { realm.copyFromRealm(ret) }
    }

    private fun like(id: String) {
        realm.executeTransactionAsync(Realm.Transaction {
            if (likeImage == null) {
                likeImage = it.createObject(LikeImage::class.java, id)
            } else {
                likeImage!!.like = !likeImage!!.like
            }
        }, Realm.Transaction.OnSuccess {
            Log.d("like","call success ${likeImage?.like}")
        })
    }

    private fun getImage(id: String) {
        ApiManager.getDetailService().getImageDetail(id, Constants.API_KEY, "ran")
            .enqueue(object : Callback<DetailResponse> {
                override fun onResponse(
                    call: Call<DetailResponse>,
                    response: Response<DetailResponse>
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

                override fun onFailure(call: Call<DetailResponse>, t: Throwable) {

                }
        })
    }
}

