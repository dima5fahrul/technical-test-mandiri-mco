package com.example.movieapp.presentation.movies

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import coil3.compose.AsyncImage
import com.example.movieapp.R
import com.example.movieapp.core.domain.model.Movie
import com.example.movieapp.presentation.common.AppendStateFooter
import com.example.movieapp.presentation.common.BackButton
import com.example.movieapp.presentation.common.Formatters
import com.example.movieapp.presentation.common.PagedContent
import com.example.movieapp.presentation.common.RatingBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    genreName: String,
    onBack: () -> Unit,
    onMovieClick: (Movie) -> Unit,
    viewModel: MovieListViewModel,
) {
    val movies = viewModel.movies.collectAsLazyPagingItems()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(genreName) },
                navigationIcon = { BackButton(onClick = onBack) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        PagedContent(
            items = movies,
            emptyMessage = R.string.movie_empty,
            modifier = Modifier.padding(innerPadding),
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(count = movies.itemCount, contentType = movies.itemContentType { "movie" }) { index ->
                    movies[index]?.let { movie -> MovieCard(movie = movie, onClick = { onMovieClick(movie) }) }
                }
                item(span = { GridItemSpan(maxLineSpan) }, contentType = "footer") {
                    AppendStateFooter(
                        loadState = movies.loadState.append,
                        showEndMessage = movies.itemCount > 0,
                        onRetry = movies::retry,
                    )
                }
            }
        }
    }
}

@Composable
private fun MovieCard(
    movie: Movie,
    onClick: () -> Unit,
) {
    Column(modifier = Modifier.clip(MaterialTheme.shapes.medium).clickable(onClick = onClick)) {
        Card(shape = MaterialTheme.shapes.medium) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                if (movie.posterUrl != null) {
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = stringResource(R.string.cd_poster, movie.title),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Text(
                        text = stringResource(R.string.no_poster),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                if (movie.rating > 0) {
                    RatingBadge(rating = movie.rating, modifier = Modifier.align(Alignment.TopStart).padding(8.dp))
                }
            }
        }
        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp),
        )
        Formatters.year(movie.releaseDate)?.let { year ->
            Text(
                text = year,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
