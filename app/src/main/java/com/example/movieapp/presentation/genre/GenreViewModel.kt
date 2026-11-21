package com.example.movieapp.presentation.genre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.core.domain.model.Genre
import com.example.movieapp.core.domain.model.Resource
import com.example.movieapp.core.domain.usecase.MovieUseCase
import com.example.movieapp.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GenreViewModel(
    private val movieUseCase: MovieUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<Genre>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Genre>>> = _uiState.asStateFlow()

    init {
        loadGenres()
    }

    fun loadGenres() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value =
                when (val result = movieUseCase.getGenres()) {
                    is Resource.Success -> if (result.data.isEmpty()) UiState.Empty else UiState.Success(result.data)
                    is Resource.Error -> UiState.Error(result.error)
                }
        }
    }
}
