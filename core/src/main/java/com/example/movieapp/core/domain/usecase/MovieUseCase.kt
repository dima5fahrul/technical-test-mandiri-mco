package com.example.movieapp.core.domain.usecase

import androidx.paging.PagingData
import com.example.movieapp.core.domain.model.Genre
import com.example.movieapp.core.domain.model.Movie
import com.example.movieapp.core.domain.model.MovieDetail
import com.example.movieapp.core.domain.model.Resource
import com.example.movieapp.core.domain.model.Review
import com.example.movieapp.core.domain.model.Trailer
import kotlinx.coroutines.flow.Flow

interface MovieUseCase {
    suspend fun getGenres(): Resource<List<Genre>>

    fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>>

    suspend fun getMovieDetail(movieId: Int): Resource<MovieDetail>

    suspend fun getTrailer(movieId: Int): Resource<Trailer?>

    fun getReviews(movieId: Int): Flow<PagingData<Review>>
}
