package com.example.movieapp.presentation.genre

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.movieapp.R
import com.example.movieapp.core.domain.model.Genre
import com.example.movieapp.presentation.common.EmptyContent
import com.example.movieapp.presentation.common.ErrorContent
import com.example.movieapp.presentation.common.LoadingContent
import com.example.movieapp.presentation.common.UiState
import com.example.movieapp.presentation.theme.GenrePalette
import com.example.movieapp.presentation.theme.TechnicalTestMovieAppTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GenreScreen(
    onGenreClick: (Genre) -> Unit,
    viewModel: GenreViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    GenreContent(uiState = uiState, onGenreClick = onGenreClick, onRetry = viewModel::loadGenres)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenreContent(
    uiState: UiState<List<Genre>>,
    onGenreClick: (Genre) -> Unit,
    onRetry: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.genre_title))
                        Text(
                            text = stringResource(R.string.genre_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (uiState) {
            UiState.Loading -> LoadingContent(modifier)
            UiState.Empty -> EmptyContent(message = R.string.genre_empty, modifier = modifier)
            is UiState.Error -> ErrorContent(error = uiState.error, onRetry = onRetry, modifier = modifier)
            is UiState.Success -> GenreGrid(genres = uiState.data, onGenreClick = onGenreClick, modifier = modifier)
        }
    }
}

@Composable
private fun GenreGrid(
    genres: List<Genre>,
    onGenreClick: (Genre) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(genres, key = { _, genre -> genre.id }) { index, genre ->
            GenreCard(
                genre = genre,
                colors = GenrePalette[index % GenrePalette.size],
                onClick = { onGenreClick(genre) },
            )
        }
    }
}

@Composable
private fun GenreCard(
    genre: Genre,
    colors: Pair<Color, Color>,
    onClick: () -> Unit,
) {
    val (start, end) = colors
    Card(onClick = onClick, shape = MaterialTheme.shapes.large) {
        Box(
            modifier =
                Modifier
                    .height(96.dp)
                    .fillMaxSize()
                    .background(Brush.linearGradient(listOf(start, end)))
                    .padding(16.dp),
            contentAlignment = Alignment.BottomStart,
        ) {
            Text(
                text = genre.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}

@Preview
@Composable
private fun GenreContentPreview() {
    TechnicalTestMovieAppTheme {
        GenreContent(
            uiState = UiState.Success(listOf(Genre(28, "Action"), Genre(35, "Comedy"), Genre(18, "Drama"))),
            onGenreClick = {},
            onRetry = {},
        )
    }
}
