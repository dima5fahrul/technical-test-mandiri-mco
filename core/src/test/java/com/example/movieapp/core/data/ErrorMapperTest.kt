package com.example.movieapp.core.data

import com.example.movieapp.core.domain.model.AppError
import kotlinx.serialization.SerializationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.UnknownHostException

class ErrorMapperTest {
    @Test
    fun `network failure maps to NoInternet`() {
        assertEquals(AppError.NoInternet, UnknownHostException("api.themoviedb.org").toAppError())
        assertEquals(AppError.NoInternet, IOException("timeout").toAppError())
    }

    @Test
    fun `http 401 maps to Unauthorized`() {
        assertEquals(AppError.Unauthorized, httpException(401).toAppError())
    }

    @Test
    fun `http 404 maps to NotFound`() {
        assertEquals(AppError.NotFound, httpException(404).toAppError())
    }

    @Test
    fun `other http errors map to Server with status code`() {
        assertEquals(AppError.Server(503), httpException(503).toAppError())
    }

    @Test
    fun `malformed json maps to Unknown`() {
        val error = SerializationException("Unexpected JSON token").toAppError()

        assertEquals(AppError.Unknown("Unexpected JSON token"), error)
    }

    @Test
    fun `AppError is passed through unchanged`() {
        assertEquals(AppError.NotFound, AppError.NotFound.toAppError())
    }

    private fun httpException(code: Int) =
        HttpException(Response.error<Any>(code, "{}".toResponseBody("application/json".toMediaType())))
}
