package com.example.movieapp.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.movieapp.R
import com.example.movieapp.core.domain.model.Genre
import com.example.movieapp.core.domain.model.MovieDetail
import com.example.movieapp.core.domain.model.Trailer
import com.example.movieapp.presentation.common.BackButton
import com.example.movieapp.presentation.common.ErrorContent
import com.example.movieapp.presentation.common.Formatters
import com.example.movieapp.presentation.common.LoadingContent
import com.example.movieapp.presentation.common.UiState
import com.example.movieapp.presentation.theme.RatingStar
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    onBack: () -> Unit,
    onReviewsClick: (MovieDetail) -> Unit,
    viewModel: MovieDetailViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val title =
        (uiState as? UiState.Success)
            ?.data
            ?.detail
            ?.title
            .orEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = { BackButton(onClick = onBack) },
            )
        },
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (val state = uiState) {
            UiState.Loading, UiState.Empty -> LoadingContent(modifier)
            is UiState.Error -> ErrorContent(error = state.error, onRetry = viewModel::loadMovie, modifier = modifier)
            is UiState.Success ->
                MovieDetailBody(
                    detail = state.data.detail,
                    trailer = state.data.trailer,
                    onReviewsClick = { onReviewsClick(state.data.detail) },
                    modifier = modifier,
                )
        }
    }
}

@Composable
private fun MovieDetailBody(
    detail: MovieDetail,
    trailer: Trailer?,
    onReviewsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Backdrop(url = detail.backdropUrl)
        DetailHeader(detail = detail, modifier = Modifier.offset(y = (-48).dp))
        Column(
            modifier = Modifier.padding(horizontal = 16.dp).offset(y = (-32).dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            if (detail.genres.isNotEmpty()) GenreTags(detail.genres)
            Section(title = stringResource(R.string.detail_overview)) {
                detail.tagline?.let { tagline ->
                    Text(
                        text = tagline,
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(
                    text = detail.overview.ifBlank { stringResource(R.string.detail_no_overview) },
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Section(title = stringResource(R.string.detail_trailer)) { TrailerSection(trailer) }
            ReviewsEntry(onClick = onReviewsClick)
        }
    }
}

@Composable
private fun Backdrop(url: String?) {
    val background = MaterialTheme.colorScheme.background
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(0.5f to Color.Transparent, 1f to background)),
        )
    }
}

@Composable
private fun DetailHeader(
    detail: MovieDetail,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Card(shape = MaterialTheme.shapes.medium, modifier = Modifier.width(110.dp).aspectRatio(2f / 3f)) {
            AsyncImage(
                model = detail.posterUrl,
                contentDescription = stringResource(R.string.cd_poster, detail.title),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
            )
        }
        Column(
            modifier = Modifier.padding(start = 16.dp).weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(text = detail.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            val meta =
                listOfNotNull(
                    Formatters.displayDate(detail.releaseDate),
                    Formatters.runtime(detail.runtimeMinutes),
                    detail.status,
                ).joinToString(" • ")
            if (meta.isNotEmpty()) {
                Text(
                    text = meta,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = RatingStar, modifier = Modifier.size(18.dp))
                Text(
                    text = Formatters.rating(detail.rating),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 4.dp),
                )
                Text(
                    text =
                        stringResource(
                            R.string.detail_votes,
                            NumberFormat.getIntegerInstance().format(detail.voteCount),
                        ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GenreTags(genres: List<Genre>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        genres.forEach { genre ->
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Text(
                    text = genre.name,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        content()
    }
}

@Composable
private fun TrailerSection(trailer: Trailer?) {
    if (trailer == null) {
        Text(
            text = stringResource(R.string.detail_no_trailer),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        return
    }
    val uriHandler = LocalUriHandler.current
    YouTubeTrailerPlayer(videoKey = trailer.key)
    Spacer(Modifier.height(4.dp))
    OutlinedButton(onClick = { uriHandler.openUri(trailer.youtubeUrl) }) {
        Icon(Icons.Filled.PlayArrow, contentDescription = null)
        Text(text = stringResource(R.string.detail_watch_on_youtube), modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun ReviewsEntry(onClick: () -> Unit) {
    Card(onClick = onClick, shape = MaterialTheme.shapes.large) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.detail_reviews),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.detail_reviews_action),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}
