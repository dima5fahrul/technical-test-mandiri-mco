package com.example.movieapp.presentation.review

import androidx.paging.testing.asSnapshot
import com.example.movieapp.core.domain.model.Review
import com.example.movieapp.core.domain.usecase.MovieUseCase
import com.example.movieapp.util.MainDispatcherRule
import com.example.movieapp.util.TestData
import com.example.movieapp.util.asPagingFlow
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ReviewViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val movieUseCase = mockk<MovieUseCase>()

    @Test
    fun `exposes reviews of the movie`() =
        runTest {
            every { movieUseCase.getReviews(603) } returns TestData.reviews.asPagingFlow()

            val viewModel = ReviewViewModel(603, movieUseCase)

            assertEquals(TestData.reviews, viewModel.reviews.asSnapshot())
        }

    @Test
    fun `movie without reviews exposes empty list`() =
        runTest {
            every { movieUseCase.getReviews(603) } returns emptyList<Review>().asPagingFlow()

            val viewModel = ReviewViewModel(603, movieUseCase)

            assertTrue(viewModel.reviews.asSnapshot().isEmpty())
        }
}
