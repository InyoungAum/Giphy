package com.inyoung.giphy.network

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ApiManager {
    private val BASE_URL = "https://api.giphy.com/v1/gifs/"
    private val GSON_BUILDER = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    private val GSON = GSON_BUILDER.create()
    private val GSON_CONVERTER_FACTORY = GsonConverterFactory.create(GSON)

    private val HTTP_CLIENT = OkHttpClient.Builder()
        .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val RETROFIT = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(HTTP_CLIENT)
        .addConverterFactory(GSON_CONVERTER_FACTORY).build()

    public fun getSearchService() = RETROFIT.create(SearchService::class.java)
}