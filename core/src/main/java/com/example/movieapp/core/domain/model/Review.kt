package com.example.movieapp.core.domain.model

data class Review(
    val id: String,
    val author: String,
    val avatarUrl: String?,
    val rating: Double?,
    val content: String,
    val createdAt: String?,
)
