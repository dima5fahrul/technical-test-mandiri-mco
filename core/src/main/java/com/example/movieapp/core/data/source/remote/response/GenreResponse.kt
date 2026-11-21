package com.example.movieapp.core.data.source.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class GenreListResponse(
    val genres: List<GenreResponse> = emptyList(),
)

@Serializable
data class GenreResponse(
    val id: Int,
    val name: String = "",
)
