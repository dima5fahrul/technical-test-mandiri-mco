package com.example.movieapp.presentation.movies

import androidx.paging.testing.asSnapshot
import com.example.movieapp.core.domain.usecase.MovieUseCase
import com.example.movieapp.util.MainDispatcherRule
import com.example.movieapp.util.TestData
import com.example.movieapp.util.asPagingFlow
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MovieListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val movieUseCase = mockk<MovieUseCase>()

    @Test
    fun `exposes movies of the selected genre`() =
        runTest {
            every { movieUseCase.getMoviesByGenre(28) } returns TestData.movies.asPagingFlow()

            val viewModel = MovieListViewModel(28, movieUseCase)

            assertEquals(TestData.movies, viewModel.movies.asSnapshot())
            verify(exactly = 1) { movieUseCase.getMoviesByGenre(28) }
        }
}
