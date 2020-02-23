package com.inyoung.giphy.network

import com.inyoung.giphy.model.ImageResponse
import com.inyoung.giphy.model.ImageListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ImageService {
    @GET("{id}")
    fun getSingleImage(
        @Path("id") id: String,
        @Query("api_key") apiKey: String,
        @Query("random_id") randomId: String
    ): Call<ImageResponse>

    @GET("search")
    fun getImagesByQuery(
        @Query("api_key") apiKey: String,
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("rating") rating: String,
        @Query("lang") lang: String,
        @Query("random_id") randomId: String
    ): Call<ImageListResponse>

    @GET("")
    fun getImagesById(
        @Query("api_key") apiKey: String,
        @Query("ids") ids: String,
        @Query("random_id") randomId: String
    ): Call<ImageListResponse>
}