package com.example.movieapp.core.di

import com.example.movieapp.core.data.MovieRepository
import com.example.movieapp.core.data.source.remote.RemoteDataSource
import com.example.movieapp.core.domain.repository.IMovieRepository
import org.koin.dsl.module

val repositoryModule =
    module {
        single { RemoteDataSource(get()) }
        single<IMovieRepository> { MovieRepository(get()) }
    }
