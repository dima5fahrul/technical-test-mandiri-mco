package com.example.movieapp.di

import com.example.movieapp.core.domain.usecase.MovieInteractor
import com.example.movieapp.core.domain.usecase.MovieUseCase
import com.example.movieapp.presentation.detail.MovieDetailViewModel
import com.example.movieapp.presentation.genre.GenreViewModel
import com.example.movieapp.presentation.movies.MovieListViewModel
import com.example.movieapp.presentation.review.ReviewViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val useCaseModule =
    module {
        factory<MovieUseCase> { MovieInteractor(get()) }
    }

val viewModelModule =
    module {
        viewModel { GenreViewModel(get()) }
        viewModel { (genreId: Int) -> MovieListViewModel(genreId, get()) }
        viewModel { (movieId: Int) -> MovieDetailViewModel(movieId, get()) }
        viewModel { (movieId: Int) -> ReviewViewModel(movieId, get()) }
    }
