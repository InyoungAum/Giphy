package com.inyoung.giphy.network

import com.inyoung.giphy.model.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface SearchService {
    @GET("search")
    fun checkAppVersion(
        @Query("api_key") apiKey: String,
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("rating") rating: String,
        @Query("lang") lang: String,
        @Query("random_id") randomId: String
    ): Call<SearchResponse>
}