package com.example.movieapp.core.domain.model

data class Trailer(
    val key: String,
    val name: String,
) {
    val youtubeUrl: String get() = "https://www.youtube.com/watch?v=$key"
}
