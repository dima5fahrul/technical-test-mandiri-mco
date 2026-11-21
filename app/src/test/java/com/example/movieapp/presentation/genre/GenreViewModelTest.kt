package com.example.movieapp.presentation.genre

import com.example.movieapp.core.domain.model.AppError
import com.example.movieapp.core.domain.model.Genre
import com.example.movieapp.core.domain.model.Resource
import com.example.movieapp.core.domain.usecase.MovieUseCase
import com.example.movieapp.presentation.common.UiState
import com.example.movieapp.util.MainDispatcherRule
import com.example.movieapp.util.TestData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GenreViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val movieUseCase = mockk<MovieUseCase>()

    @Test
    fun `shows loading while genres are being fetched`() =
        runTest {
            val response = CompletableDeferred<Resource<List<Genre>>>()
            coEvery { movieUseCase.getGenres() } coAnswers { response.await() }

            val viewModel = GenreViewModel(movieUseCase)
            assertEquals(UiState.Loading, viewModel.uiState.value)

            response.complete(Resource.Success(TestData.genres))
            assertEquals(UiState.Success(TestData.genres), viewModel.uiState.value)
        }

    @Test
    fun `shows genres when request succeeds`() =
        runTest {
            coEvery { movieUseCase.getGenres() } returns Resource.Success(TestData.genres)

            val viewModel = GenreViewModel(movieUseCase)

            assertEquals(UiState.Success(TestData.genres), viewModel.uiState.value)
        }

    @Test
    fun `shows empty state when there are no genres`() =
        runTest {
            coEvery { movieUseCase.getGenres() } returns Resource.Success(emptyList())

            val viewModel = GenreViewModel(movieUseCase)

            assertEquals(UiState.Empty, viewModel.uiState.value)
        }

    @Test
    fun `shows error when request fails`() =
        runTest {
            coEvery { movieUseCase.getGenres() } returns Resource.Error(AppError.NoInternet)

            val viewModel = GenreViewModel(movieUseCase)

            assertEquals(UiState.Error(AppError.NoInternet), viewModel.uiState.value)
        }

    @Test
    fun `retry after failure shows genres`() =
        runTest {
            coEvery { movieUseCase.getGenres() } returnsMany
                listOf(Resource.Error(AppError.NoInternet), Resource.Success(TestData.genres))
            val viewModel = GenreViewModel(movieUseCase)

            viewModel.loadGenres()

            assertEquals(UiState.Success(TestData.genres), viewModel.uiState.value)
            coVerify(exactly = 2) { movieUseCase.getGenres() }
        }
}
