package com.example.movieapp.core.data.source.remote.network

import com.example.movieapp.core.domain.model.AppError

sealed interface ApiResponse<out T> {
    data class Success<T>(
        val data: T,
    ) : ApiResponse<T>

    data object Empty : ApiResponse<Nothing>

    data class Error(
        val error: AppError,
    ) : ApiResponse<Nothing>
}
