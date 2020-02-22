package com.inyoung.giphy.activity

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.inyoung.giphy.Constants
import com.inyoung.giphy.R
import com.inyoung.giphy.model.DetailResponse
import com.inyoung.giphy.network.ApiManager
import kotlinx.android.synthetic.main.activity_detail_gif.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailGifActivity : AppCompatActivity() {
    private val imageView by lazy { image_detail }
    private val titleText by lazy { text_title }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_gif)
        val imageId = intent.getStringExtra(Constants.KEY_IMAGE_ID)
        getImage(imageId)
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

