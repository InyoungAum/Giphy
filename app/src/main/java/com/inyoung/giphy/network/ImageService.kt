package com.inyoung.giphy.network

import com.inyoung.giphy.Constants
import com.inyoung.giphy.model.ImageResponse
import com.inyoung.giphy.model.ImageListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ImageService {
    @GET("gifs/{id}")
    fun getSingleImage(
        @Path("id") id: String,
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("random_id") randomId: String = ""
    ): Call<ImageResponse>

    @GET("gifs/search")
    fun getImagesByQuery(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("rating") rating: String = "",
        @Query("lang") lang: String = "ko",
        @Query("random_id") randomId: String = ""
    ): Call<ImageListResponse>

    @GET("gifs")
    fun getImagesById(
        @Query("ids") ids: String,
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("random_id") randomId: String = ""
    ): Call<ImageListResponse>
}