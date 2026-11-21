package com.example.movieapp.core.domain.usecase

import androidx.paging.PagingData
import com.example.movieapp.core.domain.model.Genre
import com.example.movieapp.core.domain.model.Movie
import com.example.movieapp.core.domain.model.MovieDetail
import com.example.movieapp.core.domain.model.Resource
import com.example.movieapp.core.domain.model.Review
import com.example.movieapp.core.domain.model.Trailer
import com.example.movieapp.core.domain.repository.IMovieRepository
import kotlinx.coroutines.flow.Flow

class MovieInteractor(
    private val movieRepository: IMovieRepository,
) : MovieUseCase {
    override suspend fun getGenres(): Resource<List<Genre>> = movieRepository.getGenres()

    override fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>> = movieRepository.getMoviesByGenre(genreId)

    override suspend fun getMovieDetail(movieId: Int): Resource<MovieDetail> = movieRepository.getMovieDetail(movieId)

    override suspend fun getTrailer(movieId: Int): Resource<Trailer?> = movieRepository.getTrailer(movieId)

    override fun getReviews(movieId: Int): Flow<PagingData<Review>> = movieRepository.getReviews(movieId)
}
