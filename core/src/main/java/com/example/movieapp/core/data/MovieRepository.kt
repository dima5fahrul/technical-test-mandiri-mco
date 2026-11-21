package com.example.movieapp.core.data

import androidx.paging.PagingData
import androidx.paging.map
import com.example.movieapp.core.data.source.remote.RemoteDataSource
import com.example.movieapp.core.data.source.remote.network.ApiResponse
import com.example.movieapp.core.domain.model.AppError
import com.example.movieapp.core.domain.model.Genre
import com.example.movieapp.core.domain.model.Movie
import com.example.movieapp.core.domain.model.MovieDetail
import com.example.movieapp.core.domain.model.Resource
import com.example.movieapp.core.domain.model.Review
import com.example.movieapp.core.domain.model.Trailer
import com.example.movieapp.core.domain.repository.IMovieRepository
import com.example.movieapp.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieRepository(
    private val remoteDataSource: RemoteDataSource,
) : IMovieRepository {
    override suspend fun getGenres(): Resource<List<Genre>> =
        remoteDataSource.getGenres().toResource(empty = Resource.Success(emptyList())) { genres ->
            genres.map(DataMapper::mapGenre)
        }

    override fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>> =
        remoteDataSource.getMoviesByGenre(genreId).map { pagingData -> pagingData.map(DataMapper::mapMovie) }

    override suspend fun getMovieDetail(movieId: Int): Resource<MovieDetail> =
        remoteDataSource
            .getMovieDetail(
                movieId,
            ).toResource(empty = Resource.Error(AppError.NotFound), DataMapper::mapMovieDetail)

    override suspend fun getTrailer(movieId: Int): Resource<Trailer?> =
        remoteDataSource.getVideos(movieId).toResource(empty = Resource.Success(null), DataMapper::pickTrailer)

    override fun getReviews(movieId: Int): Flow<PagingData<Review>> =
        remoteDataSource.getReviews(movieId).map { pagingData -> pagingData.map(DataMapper::mapReview) }

    private inline fun <T, R> ApiResponse<T>.toResource(
        empty: Resource<R>,
        transform: (T) -> R,
    ): Resource<R> =
        when (this) {
            is ApiResponse.Success -> Resource.Success(transform(data))
            ApiResponse.Empty -> empty
            is ApiResponse.Error -> Resource.Error(error)
        }
}
