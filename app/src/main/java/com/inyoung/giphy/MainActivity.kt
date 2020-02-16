package com.inyoung.giphy

import android.app.Activity
import android.os.Bundle
import com.inyoung.giphy.Constants.API_KEY
import com.inyoung.giphy.model.SearchResponse
import com.inyoung.giphy.network.ApiManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        search("car")
    }

    private fun search(query: String) {
        ApiManager.getSearchService().checkAppVersion(
            API_KEY,
            query,
            10,
            0,
            "",
            "ko",
            "ran"
        ).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {

            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {

            }
        })
    }
}