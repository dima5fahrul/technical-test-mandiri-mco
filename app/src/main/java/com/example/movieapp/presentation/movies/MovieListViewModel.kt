package com.example.movieapp.presentation.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.movieapp.core.domain.model.Movie
import com.example.movieapp.core.domain.usecase.MovieUseCase
import kotlinx.coroutines.flow.Flow

class MovieListViewModel(
    genreId: Int,
    movieUseCase: MovieUseCase,
) : ViewModel() {
    val movies: Flow<PagingData<Movie>> = movieUseCase.getMoviesByGenre(genreId).cachedIn(viewModelScope)
}
