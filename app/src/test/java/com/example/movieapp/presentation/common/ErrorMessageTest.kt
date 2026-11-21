package com.example.movieapp.presentation.common

import com.example.movieapp.R
import com.example.movieapp.core.domain.model.AppError
import org.junit.Assert.assertEquals
import org.junit.Test

class ErrorMessageTest {
    @Test
    fun `each app error has its own message`() {
        assertEquals(R.string.error_no_internet, AppError.NoInternet.messageRes())
        assertEquals(R.string.error_unauthorized, AppError.Unauthorized.messageRes())
        assertEquals(R.string.error_not_found, AppError.NotFound.messageRes())
        assertEquals(R.string.error_server, AppError.Server(500).messageRes())
        assertEquals(R.string.error_unknown, AppError.Unknown("boom").messageRes())
    }

    @Test
    fun `paging throwable that is not an AppError becomes Unknown`() {
        assertEquals(AppError.Unknown("boom"), IllegalStateException("boom").asAppError())
        assertEquals(AppError.NotFound, AppError.NotFound.asAppError())
    }
}
