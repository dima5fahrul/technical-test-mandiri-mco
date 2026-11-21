package com.example.movieapp.core.domain.model

sealed interface Resource<out T> {
    data class Success<T>(
        val data: T,
    ) : Resource<T>

    data class Error(
        val error: AppError,
    ) : Resource<Nothing>
}
