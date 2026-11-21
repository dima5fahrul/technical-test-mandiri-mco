package com.example.movieapp.core.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.movieapp.core.data.paging.TmdbPagingSource
import com.example.movieapp.core.data.source.remote.network.ApiService
import com.example.movieapp.core.data.source.remote.response.PagedResponse
import com.example.movieapp.core.domain.model.Genre
import com.example.movieapp.core.domain.model.Movie
import com.example.movieapp.core.domain.model.MovieDetail
import com.example.movieapp.core.domain.model.Resource
import com.example.movieapp.core.domain.model.Review
import com.example.movieapp.core.domain.model.Trailer
import com.example.movieapp.core.domain.repository.IMovieRepository
import com.example.movieapp.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.cancellation.CancellationException

class MovieRepository(
    private val apiService: ApiService,
) : IMovieRepository {
    override suspend fun getGenres(): Resource<List<Genre>> =
        safeCall { apiService.getGenres().genres.map(DataMapper::mapGenre) }

    override fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>> =
        pagerOf { page -> apiService.discoverMovies(genreId = genreId, page = page).mapResults(DataMapper::mapMovie) }

    override suspend fun getMovieDetail(movieId: Int): Resource<MovieDetail> =
        safeCall { DataMapper.mapMovieDetail(apiService.getMovieDetail(movieId)) }

    override suspend fun getTrailer(movieId: Int): Resource<Trailer?> =
        safeCall { DataMapper.pickTrailer(apiService.getVideos(movieId).results) }

    override fun getReviews(movieId: Int): Flow<PagingData<Review>> =
        pagerOf { page -> apiService.getReviews(movieId = movieId, page = page).mapResults(DataMapper::mapReview) }

    private suspend fun <T> safeCall(block: suspend () -> T): Resource<T> =
        try {
            Resource.Success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(e.toAppError())
        }

    private fun <T : Any> pagerOf(fetchPage: suspend (page: Int) -> PagedResponse<T>): Flow<PagingData<T>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { TmdbPagingSource(fetchPage) },
        ).flow

    private fun <T, R> PagedResponse<T>.mapResults(transform: (T) -> R): PagedResponse<R> =
        PagedResponse(
            page = page,
            results = results.map(transform),
            totalPages = totalPages,
            totalResults = totalResults,
        )

    private companion object {
        const val PAGE_SIZE = 20
    }
}
