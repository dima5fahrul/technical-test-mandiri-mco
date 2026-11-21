package com.example.movieapp.core.data.source.remote

import androidx.paging.testing.asSnapshot
import com.example.movieapp.core.data.source.remote.network.ApiResponse
import com.example.movieapp.core.data.source.remote.network.ApiService
import com.example.movieapp.core.data.source.remote.response.GenreListResponse
import com.example.movieapp.core.data.source.remote.response.GenreResponse
import com.example.movieapp.core.data.source.remote.response.MovieDetailResponse
import com.example.movieapp.core.data.source.remote.response.MovieResponse
import com.example.movieapp.core.data.source.remote.response.PagedResponse
import com.example.movieapp.core.data.source.remote.response.ReviewResponse
import com.example.movieapp.core.data.source.remote.response.VideoListResponse
import com.example.movieapp.core.data.source.remote.response.VideoResponse
import com.example.movieapp.core.domain.model.AppError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class RemoteDataSourceTest {
    private val apiService = mockk<ApiService>()
    private val remoteDataSource = RemoteDataSource(apiService)

    @Test
    fun `getGenres returns Success with genres`() =
        runTest {
            coEvery { apiService.getGenres() } returns GenreListResponse(listOf(GenreResponse(28, "Action")))

            assertEquals(ApiResponse.Success(listOf(GenreResponse(28, "Action"))), remoteDataSource.getGenres())
        }

    @Test
    fun `getGenres returns Empty when there are no genres`() =
        runTest {
            coEvery { apiService.getGenres() } returns GenreListResponse(emptyList())

            assertEquals(ApiResponse.Empty, remoteDataSource.getGenres())
        }

    @Test
    fun `getGenres without connection returns NoInternet error`() =
        runTest {
            coEvery { apiService.getGenres() } throws IOException("offline")

            assertEquals(ApiResponse.Error(AppError.NoInternet), remoteDataSource.getGenres())
        }

    @Test
    fun `getMovieDetail returns Success with detail`() =
        runTest {
            val detail = MovieDetailResponse(id = 603, title = "The Matrix")
            coEvery { apiService.getMovieDetail(603) } returns detail

            assertEquals(ApiResponse.Success(detail), remoteDataSource.getMovieDetail(603))
        }

    @Test
    fun `getMovieDetail for unknown movie returns NotFound error`() =
        runTest {
            coEvery { apiService.getMovieDetail(any()) } throws httpException(404)

            assertEquals(ApiResponse.Error(AppError.NotFound), remoteDataSource.getMovieDetail(-1))
        }

    @Test
    fun `getVideos returns Success with videos`() =
        runTest {
            val videos = listOf(VideoResponse(key = "abc", site = "YouTube", type = "Trailer"))
            coEvery { apiService.getVideos(603) } returns VideoListResponse(videos)

            assertEquals(ApiResponse.Success(videos), remoteDataSource.getVideos(603))
        }

    @Test
    fun `getVideos returns Empty when movie has no videos`() =
        runTest {
            coEvery { apiService.getVideos(603) } returns VideoListResponse(emptyList())

            assertEquals(ApiResponse.Empty, remoteDataSource.getVideos(603))
        }

    @Test
    fun `getVideos with invalid token returns Unauthorized error`() =
        runTest {
            coEvery { apiService.getVideos(any()) } throws httpException(401)

            assertEquals(ApiResponse.Error(AppError.Unauthorized), remoteDataSource.getVideos(603))
        }

    @Test
    fun `getMoviesByGenre pages discover results for the genre`() =
        runTest {
            coEvery { apiService.discoverMovies(genreId = 28, page = any()) } returns
                PagedResponse(page = 1, results = listOf(MovieResponse(1, "Dune")), totalPages = 1)

            val movies = remoteDataSource.getMoviesByGenre(28).asSnapshot()

            assertEquals(listOf(MovieResponse(1, "Dune")), movies)
            coVerify { apiService.discoverMovies(genreId = 28, page = 1) }
        }

    @Test
    fun `getReviews pages reviews for the movie`() =
        runTest {
            coEvery { apiService.getReviews(movieId = 603, page = any()) } returns
                PagedResponse(page = 1, results = listOf(ReviewResponse(id = "r1", author = "jane")), totalPages = 1)

            val reviews = remoteDataSource.getReviews(603).asSnapshot()

            assertEquals(listOf("jane"), reviews.map { it.author })
        }

    @Test
    fun `getReviews for movie without reviews is empty`() =
        runTest {
            coEvery { apiService.getReviews(any(), any()) } returns PagedResponse(page = 1, totalPages = 0)

            assertTrue(remoteDataSource.getReviews(603).asSnapshot().isEmpty())
        }

    private fun httpException(code: Int) =
        HttpException(Response.error<Any>(code, "{}".toResponseBody("application/json".toMediaType())))
}
