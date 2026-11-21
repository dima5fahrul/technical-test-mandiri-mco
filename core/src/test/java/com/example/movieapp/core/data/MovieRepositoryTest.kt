package com.example.movieapp.core.data

import androidx.paging.testing.asSnapshot
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
import com.example.movieapp.core.domain.model.Genre
import com.example.movieapp.core.domain.model.Resource
import com.example.movieapp.core.domain.model.Trailer
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

class MovieRepositoryTest {
    private val apiService = mockk<ApiService>()
    private val repository = MovieRepository(apiService)

    @Test
    fun `getGenres returns mapped genres`() =
        runTest {
            coEvery { apiService.getGenres() } returns
                GenreListResponse(listOf(GenreResponse(28, "Action"), GenreResponse(35, "Comedy")))

            val result = repository.getGenres()

            assertEquals(Resource.Success(listOf(Genre(28, "Action"), Genre(35, "Comedy"))), result)
        }

    @Test
    fun `getGenres without connection returns NoInternet error`() =
        runTest {
            coEvery { apiService.getGenres() } throws IOException("offline")

            assertEquals(Resource.Error(AppError.NoInternet), repository.getGenres())
        }

    @Test
    fun `getMovieDetail returns mapped detail`() =
        runTest {
            coEvery { apiService.getMovieDetail(603) } returns MovieDetailResponse(id = 603, title = "The Matrix")

            val result = repository.getMovieDetail(603) as Resource.Success

            assertEquals(603, result.data.id)
            assertEquals("The Matrix", result.data.title)
        }

    @Test
    fun `getMovieDetail for unknown movie returns NotFound error`() =
        runTest {
            coEvery { apiService.getMovieDetail(any()) } throws httpException(404)

            assertEquals(Resource.Error(AppError.NotFound), repository.getMovieDetail(-1))
        }

    @Test
    fun `getTrailer returns the best youtube trailer`() =
        runTest {
            coEvery { apiService.getVideos(603) } returns
                VideoListResponse(
                    listOf(VideoResponse(key = "abc", name = "Official Trailer", site = "YouTube", type = "Trailer")),
                )

            assertEquals(Resource.Success(Trailer("abc", "Official Trailer")), repository.getTrailer(603))
        }

    @Test
    fun `getTrailer returns null data when movie has no videos`() =
        runTest {
            coEvery { apiService.getVideos(603) } returns VideoListResponse(emptyList())

            assertEquals(Resource.Success(null), repository.getTrailer(603))
        }

    @Test
    fun `getTrailer with invalid token returns Unauthorized error`() =
        runTest {
            coEvery { apiService.getVideos(any()) } throws httpException(401)

            assertEquals(Resource.Error(AppError.Unauthorized), repository.getTrailer(603))
        }

    @Test
    fun `getMoviesByGenre pages discover results for the genre`() =
        runTest {
            coEvery { apiService.discoverMovies(genreId = 28, page = any()) } returns
                PagedResponse(page = 1, results = listOf(MovieResponse(1, "Dune")), totalPages = 1)

            val movies = repository.getMoviesByGenre(28).asSnapshot()

            assertEquals(listOf("Dune"), movies.map { it.title })
            coVerify { apiService.discoverMovies(genreId = 28, page = 1) }
        }

    @Test
    fun `getReviews pages reviews for the movie`() =
        runTest {
            coEvery { apiService.getReviews(movieId = 603, page = any()) } returns
                PagedResponse(page = 1, results = listOf(ReviewResponse(id = "r1", author = "jane")), totalPages = 1)

            val reviews = repository.getReviews(603).asSnapshot()

            assertEquals(listOf("jane"), reviews.map { it.author })
        }

    @Test
    fun `getReviews for movie without reviews is empty`() =
        runTest {
            coEvery { apiService.getReviews(any(), any()) } returns PagedResponse(page = 1, totalPages = 0)

            assertTrue(repository.getReviews(603).asSnapshot().isEmpty())
        }

    private fun httpException(code: Int) =
        HttpException(Response.error<Any>(code, "{}".toResponseBody("application/json".toMediaType())))
}
