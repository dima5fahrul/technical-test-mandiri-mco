package com.example.movieapp.presentation.detail

import com.example.movieapp.core.domain.model.AppError
import com.example.movieapp.core.domain.model.Resource
import com.example.movieapp.core.domain.usecase.MovieUseCase
import com.example.movieapp.presentation.common.UiState
import com.example.movieapp.util.MainDispatcherRule
import com.example.movieapp.util.TestData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MovieDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val movieUseCase = mockk<MovieUseCase>()

    @Test
    fun `shows detail with trailer when both requests succeed`() =
        runTest {
            coEvery { movieUseCase.getMovieDetail(603) } returns Resource.Success(TestData.movieDetail)
            coEvery { movieUseCase.getTrailer(603) } returns Resource.Success(TestData.trailer)

            val viewModel = MovieDetailViewModel(603, movieUseCase)

            assertEquals(
                UiState.Success(MovieDetailContent(TestData.movieDetail, TestData.trailer)),
                viewModel.uiState.value,
            )
        }

    @Test
    fun `shows detail without trailer when movie has none`() =
        runTest {
            coEvery { movieUseCase.getMovieDetail(603) } returns Resource.Success(TestData.movieDetail)
            coEvery { movieUseCase.getTrailer(603) } returns Resource.Success(null)

            val viewModel = MovieDetailViewModel(603, movieUseCase)

            assertEquals(UiState.Success(MovieDetailContent(TestData.movieDetail, null)), viewModel.uiState.value)
        }

    @Test
    fun `trailer failure still shows movie detail`() =
        runTest {
            coEvery { movieUseCase.getMovieDetail(603) } returns Resource.Success(TestData.movieDetail)
            coEvery { movieUseCase.getTrailer(603) } returns Resource.Error(AppError.Server(500))

            val viewModel = MovieDetailViewModel(603, movieUseCase)

            assertEquals(UiState.Success(MovieDetailContent(TestData.movieDetail, null)), viewModel.uiState.value)
        }

    @Test
    fun `detail failure shows error`() =
        runTest {
            coEvery { movieUseCase.getMovieDetail(603) } returns Resource.Error(AppError.NotFound)
            coEvery { movieUseCase.getTrailer(603) } returns Resource.Success(TestData.trailer)

            val viewModel = MovieDetailViewModel(603, movieUseCase)

            assertEquals(UiState.Error(AppError.NotFound), viewModel.uiState.value)
        }

    @Test
    fun `retry after failure shows detail`() =
        runTest {
            coEvery { movieUseCase.getMovieDetail(603) } returnsMany
                listOf(Resource.Error(AppError.NoInternet), Resource.Success(TestData.movieDetail))
            coEvery { movieUseCase.getTrailer(603) } returns Resource.Success(TestData.trailer)
            val viewModel = MovieDetailViewModel(603, movieUseCase)

            viewModel.loadMovie()

            assertEquals(
                UiState.Success(MovieDetailContent(TestData.movieDetail, TestData.trailer)),
                viewModel.uiState.value,
            )
        }
}
