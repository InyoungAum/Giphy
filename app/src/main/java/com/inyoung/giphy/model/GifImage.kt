package com.inyoung.giphy.model

data class GifImage(
    val type: String,
    val id: String,
    val url: String,
    val title: String,
    val importDatetime: String,
    val images: Image?
) {
    constructor() : this("", "", "" ,"", "" , null)
}