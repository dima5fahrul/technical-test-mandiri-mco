package com.example.movieapp.core.domain.model

sealed class AppError : Exception() {
    data object NoInternet : AppError()

    data object Unauthorized : AppError()

    data object NotFound : AppError()

    data class Server(
        val code: Int,
    ) : AppError()

    data class Unknown(
        override val message: String?,
    ) : AppError()
}
