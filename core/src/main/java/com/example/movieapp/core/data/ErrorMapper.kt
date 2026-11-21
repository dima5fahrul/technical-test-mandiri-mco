package com.example.movieapp.core.data

import com.example.movieapp.core.domain.model.AppError
import retrofit2.HttpException
import java.io.IOException

fun Throwable.toAppError(): AppError =
    when (this) {
        is AppError -> this
        is IOException -> AppError.NoInternet
        is HttpException ->
            when (code()) {
                401 -> AppError.Unauthorized
                404 -> AppError.NotFound
                else -> AppError.Server(code())
            }
        else -> AppError.Unknown(message)
    }
