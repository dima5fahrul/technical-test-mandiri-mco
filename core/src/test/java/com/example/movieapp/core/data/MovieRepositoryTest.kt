package com.example.movieapp.core.data

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.example.movieapp.core.data.source.remote.RemoteDataSource
import com.example.movieapp.core.data.source.remote.network.ApiResponse
import com.example.movieapp.core.data.source.remote.response.GenreResponse
import com.example.movieapp.core.data.source.remote.response.MovieDetailResponse
import com.example.movieapp.core.data.source.remote.response.MovieResponse
import com.example.movieapp.core.data.source.remote.response.ReviewResponse
import com.example.movieapp.core.data.source.remote.response.VideoResponse
import com.example.movieapp.core.domain.model.AppError
import com.example.movieapp.core.domain.model.Genre
import com.example.movieapp.core.domain.model.Movie
import com.example.movieapp.core.domain.model.Resource
import com.example.movieapp.core.domain.model.Trailer
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class MovieRepositoryTest {
    private val remoteDataSource = mockk<RemoteDataSource>()
    private val repository = MovieRepository(remoteDataSource)

    @Test
    fun `getGenres maps genres to domain`() =
        runTest {
            coEvery { remoteDataSource.getGenres() } returns
                ApiResponse.Success(listOf(GenreResponse(28, "Action"), GenreResponse(35, "Comedy")))

            assertEquals(Resource.Success(listOf(Genre(28, "Action"), Genre(35, "Comedy"))), repository.getGenres())
        }

    @Test
    fun `getGenres maps empty response to empty list`() =
        runTest {
            coEvery { remoteDataSource.getGenres() } returns ApiResponse.Empty

            assertEquals(Resource.Success(emptyList<Genre>()), repository.getGenres())
        }

    @Test
    fun `getGenres propagates error`() =
        runTest {
            coEvery { remoteDataSource.getGenres() } returns ApiResponse.Error(AppError.NoInternet)

            assertEquals(Resource.Error(AppError.NoInternet), repository.getGenres())
        }

    @Test
    fun `getMovieDetail maps detail to domain`() =
        runTest {
            coEvery { remoteDataSource.getMovieDetail(603) } returns
                ApiResponse.Success(MovieDetailResponse(id = 603, title = "The Matrix", posterPath = "/m.jpg"))

            val result = repository.getMovieDetail(603) as Resource.Success

            assertEquals("The Matrix", result.data.title)
            assertEquals("https://image.tmdb.org/t/p/w500/m.jpg", result.data.posterUrl)
        }

    @Test
    fun `getMovieDetail propagates NotFound error`() =
        runTest {
            coEvery { remoteDataSource.getMovieDetail(any()) } returns ApiResponse.Error(AppError.NotFound)

            assertEquals(Resource.Error(AppError.NotFound), repository.getMovieDetail(-1))
        }

    @Test
    fun `getTrailer picks the best youtube trailer`() =
        runTest {
            coEvery { remoteDataSource.getVideos(603) } returns
                ApiResponse.Success(
                    listOf(
                        VideoResponse(key = "teaser", name = "Teaser", site = "YouTube", type = "Teaser"),
                        VideoResponse(key = "main", name = "Official Trailer", site = "YouTube", type = "Trailer"),
                    ),
                )

            assertEquals(Resource.Success(Trailer("main", "Official Trailer")), repository.getTrailer(603))
        }

    @Test
    fun `getTrailer returns null when movie has no videos`() =
        runTest {
            coEvery { remoteDataSource.getVideos(603) } returns ApiResponse.Empty

            assertEquals(Resource.Success(null), repository.getTrailer(603))
        }

    @Test
    fun `getTrailer propagates Unauthorized error`() =
        runTest {
            coEvery { remoteDataSource.getVideos(any()) } returns ApiResponse.Error(AppError.Unauthorized)

            assertEquals(Resource.Error(AppError.Unauthorized), repository.getTrailer(603))
        }

    @Test
    fun `getMoviesByGenre maps paged responses to movies`() =
        runTest {
            every { remoteDataSource.getMoviesByGenre(28) } returns
                flowOf(PagingData.from(listOf(MovieResponse(1, "Dune", "/dune.jpg", "2021-09-15", 7.8))))

            val movies = repository.getMoviesByGenre(28).asSnapshot()

            assertEquals(
                listOf(Movie(1, "Dune", "https://image.tmdb.org/t/p/w500/dune.jpg", "2021-09-15", 7.8)),
                movies,
            )
        }

    @Test
    fun `getReviews maps paged responses to reviews with clean text`() =
        runTest {
            every { remoteDataSource.getReviews(603) } returns
                flowOf(PagingData.from(listOf(ReviewResponse(id = "r1", author = "jane", content = "**Great** movie"))))

            val reviews = repository.getReviews(603).asSnapshot()

            assertEquals(listOf("Great movie"), reviews.map { it.content })
        }
}
