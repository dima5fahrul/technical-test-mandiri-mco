package com.example.movieapp.core.domain.usecase

import com.example.movieapp.core.domain.model.AppError
import com.example.movieapp.core.domain.model.Genre
import com.example.movieapp.core.domain.model.Resource
import com.example.movieapp.core.domain.repository.IMovieRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class MovieInteractorTest {
    private val repository = mockk<IMovieRepository>()
    private val interactor = MovieInteractor(repository)

    @Test
    fun `getGenres returns repository result`() =
        runTest {
            val genres = Resource.Success(listOf(Genre(28, "Action")))
            coEvery { repository.getGenres() } returns genres

            assertEquals(genres, interactor.getGenres())
        }

    @Test
    fun `getMovieDetail propagates repository error`() =
        runTest {
            coEvery { repository.getMovieDetail(1) } returns Resource.Error(AppError.NotFound)

            assertEquals(Resource.Error(AppError.NotFound), interactor.getMovieDetail(1))
        }
}
