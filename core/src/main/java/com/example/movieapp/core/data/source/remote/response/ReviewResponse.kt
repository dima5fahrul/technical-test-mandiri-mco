package com.example.movieapp.core.data.source.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewResponse(
    val id: String,
    val author: String = "",
    @SerialName("author_details") val authorDetails: AuthorDetailsResponse? = null,
    val content: String = "",
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class AuthorDetailsResponse(
    val name: String? = null,
    val username: String? = null,
    @SerialName("avatar_path") val avatarPath: String? = null,
    val rating: Double? = null,
)
