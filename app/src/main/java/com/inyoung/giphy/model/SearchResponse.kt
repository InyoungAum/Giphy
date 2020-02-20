package com.inyoung.giphy.model

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("data") val images: MutableList<GifImage>,
    val pagination: Pagination,
    @SerializedName("meta") val metadata: Metadata
)