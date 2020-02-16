package com.inyoung.giphy.model

import com.google.gson.annotations.SerializedName

data class Metadata(
    @SerializedName("msg") val message: String,
    val status: Int,
    val responseId: String
)