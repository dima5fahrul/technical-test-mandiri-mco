package com.example.movieapp.presentation.common

import com.example.movieapp.core.domain.model.AppError

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>

    data object Empty : UiState<Nothing>

    data class Success<T>(
        val data: T,
    ) : UiState<T>

    data class Error(
        val error: AppError,
    ) : UiState<Nothing>
}
