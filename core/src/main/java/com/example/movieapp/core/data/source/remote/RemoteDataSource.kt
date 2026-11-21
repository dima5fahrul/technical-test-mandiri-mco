package com.example.movieapp.core.data.source.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.movieapp.core.data.source.remote.network.ApiResponse
import com.example.movieapp.core.data.source.remote.network.ApiService
import com.example.movieapp.core.data.source.remote.network.toAppError
import com.example.movieapp.core.data.source.remote.paging.TmdbPagingSource
import com.example.movieapp.core.data.source.remote.response.GenreResponse
import com.example.movieapp.core.data.source.remote.response.MovieDetailResponse
import com.example.movieapp.core.data.source.remote.response.MovieResponse
import com.example.movieapp.core.data.source.remote.response.PagedResponse
import com.example.movieapp.core.data.source.remote.response.ReviewResponse
import com.example.movieapp.core.data.source.remote.response.VideoResponse
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.cancellation.CancellationException

class RemoteDataSource(
    private val apiService: ApiService,
) {
    suspend fun getGenres(): ApiResponse<List<GenreResponse>> = safeListCall { apiService.getGenres().genres }

    fun getMoviesByGenre(genreId: Int): Flow<PagingData<MovieResponse>> =
        pagerOf { page -> apiService.discoverMovies(genreId = genreId, page = page) }

    suspend fun getMovieDetail(movieId: Int): ApiResponse<MovieDetailResponse> =
        safeCall {
            apiService.getMovieDetail(movieId)
        }

    suspend fun getVideos(movieId: Int): ApiResponse<List<VideoResponse>> =
        safeListCall {
            apiService.getVideos(movieId).results
        }

    fun getReviews(movieId: Int): Flow<PagingData<ReviewResponse>> =
        pagerOf { page -> apiService.getReviews(movieId = movieId, page = page) }

    private suspend fun <T> safeCall(call: suspend () -> T): ApiResponse<T> =
        try {
            ApiResponse.Success(call())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ApiResponse.Error(e.toAppError())
        }

    private suspend fun <T> safeListCall(call: suspend () -> List<T>): ApiResponse<List<T>> =
        when (val response = safeCall(call)) {
            is ApiResponse.Success -> if (response.data.isEmpty()) ApiResponse.Empty else response
            else -> response
        }

    private fun <T : Any> pagerOf(fetchPage: suspend (page: Int) -> PagedResponse<T>): Flow<PagingData<T>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { TmdbPagingSource(fetchPage) },
        ).flow

    private companion object {
        const val PAGE_SIZE = 20
    }
}
