package com.inyoung.giphy.model

import com.google.gson.annotations.SerializedName

data class ImageResponse(
    @SerializedName("data") val image: GifImage,
    @SerializedName("meta") val metadata: Metadata
)