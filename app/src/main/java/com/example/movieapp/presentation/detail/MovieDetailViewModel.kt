package com.example.movieapp.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.core.domain.model.MovieDetail
import com.example.movieapp.core.domain.model.Resource
import com.example.movieapp.core.domain.model.Trailer
import com.example.movieapp.core.domain.usecase.MovieUseCase
import com.example.movieapp.presentation.common.UiState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MovieDetailContent(
    val detail: MovieDetail,
    val trailer: Trailer?,
)

class MovieDetailViewModel(
    private val movieId: Int,
    private val movieUseCase: MovieUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<MovieDetailContent>>(UiState.Loading)
    val uiState: StateFlow<UiState<MovieDetailContent>> = _uiState.asStateFlow()

    init {
        loadMovie()
    }

    fun loadMovie() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val trailer = async { movieUseCase.getTrailer(movieId) }
            _uiState.value =
                when (val detail = movieUseCase.getMovieDetail(movieId)) {
                    is Resource.Success -> {
                        val trailerResult = trailer.await()
                        UiState.Success(
                            MovieDetailContent(
                                detail = detail.data,
                                trailer = (trailerResult as? Resource.Success)?.data,
                            ),
                        )
                    }
                    is Resource.Error -> {
                        trailer.cancel()
                        UiState.Error(detail.error)
                    }
                }
        }
    }
}
