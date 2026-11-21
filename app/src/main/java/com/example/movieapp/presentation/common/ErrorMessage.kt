package com.example.movieapp.presentation.common

import androidx.annotation.StringRes
import com.example.movieapp.R
import com.example.movieapp.core.domain.model.AppError

@StringRes
fun AppError.messageRes(): Int =
    when (this) {
        AppError.NoInternet -> R.string.error_no_internet
        AppError.Unauthorized -> R.string.error_unauthorized
        AppError.NotFound -> R.string.error_not_found
        is AppError.Server -> R.string.error_server
        is AppError.Unknown -> R.string.error_unknown
    }

fun Throwable.asAppError(): AppError = this as? AppError ?: AppError.Unknown(message)
