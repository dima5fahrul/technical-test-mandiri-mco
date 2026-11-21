package com.example.movieapp.core.data.source.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class VideoListResponse(
    val results: List<VideoResponse> = emptyList(),
)

@Serializable
data class VideoResponse(
    val key: String = "",
    val name: String = "",
    val site: String = "",
    val type: String = "",
    val official: Boolean = false,
)
