package com.inyoung.giphy.model

data class Image(
    val fixedHeight: ImageMetadata,
    val fixedHeightStill: ImageMetadata,
    val fixedWidth: ImageMetadata,
    val fixedWidthStill: ImageMetadata
)