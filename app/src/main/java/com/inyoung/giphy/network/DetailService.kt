package com.inyoung.giphy.network

import com.inyoung.giphy.model.DetailResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface DetailService {
    @GET("{id}")
    fun getImageDetail(
        @Path("id") id: String,
        @Query("api_key") apiKey: String,
        @Query("random_id") randomId: String
    ): Call<DetailResponse>
}