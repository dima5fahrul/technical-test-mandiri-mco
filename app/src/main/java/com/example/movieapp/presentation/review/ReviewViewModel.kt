package com.example.movieapp.presentation.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.movieapp.core.domain.model.Review
import com.example.movieapp.core.domain.usecase.MovieUseCase
import kotlinx.coroutines.flow.Flow

class ReviewViewModel(
    movieId: Int,
    movieUseCase: MovieUseCase,
) : ViewModel() {
    val reviews: Flow<PagingData<Review>> = movieUseCase.getReviews(movieId).cachedIn(viewModelScope)
}
