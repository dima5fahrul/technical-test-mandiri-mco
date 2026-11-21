package com.example.movieapp.presentation.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.example.movieapp.R
import com.example.movieapp.core.domain.model.Review
import com.example.movieapp.presentation.common.AppendStateFooter
import com.example.movieapp.presentation.common.BackButton
import com.example.movieapp.presentation.common.Formatters
import com.example.movieapp.presentation.common.PagedContent
import com.example.movieapp.presentation.common.RatingBadge

private const val COLLAPSED_MAX_LINES = 6

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    movieTitle: String,
    onBack: () -> Unit,
    viewModel: ReviewViewModel,
) {
    val reviews = viewModel.reviews.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.review_title))
                        Text(
                            text = movieTitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
                navigationIcon = { BackButton(onClick = onBack) },
            )
        },
    ) { innerPadding ->
        PagedContent(
            items = reviews,
            emptyMessage = R.string.review_empty,
            modifier = Modifier.padding(innerPadding),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    count = reviews.itemCount,
                    key = reviews.itemKey { it.id },
                    contentType = reviews.itemContentType { "review" },
                ) { index ->
                    reviews[index]?.let { review -> ReviewCard(review) }
                }
                item(contentType = "footer") {
                    AppendStateFooter(
                        loadState = reviews.loadState.append,
                        showEndMessage = reviews.itemCount > 0,
                        onRetry = reviews::retry,
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewCard(review: Review) {
    var expanded by rememberSaveable(review.id) { mutableStateOf(false) }
    var overflowing by rememberSaveable(review.id) { mutableStateOf(false) }

    Card(shape = MaterialTheme.shapes.large) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(name = review.author, url = review.avatarUrl)
                Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                    Text(
                        text = review.author,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Formatters.displayDate(review.createdAt)?.let { date ->
                        Text(
                            text = date,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                review.rating?.let { RatingBadge(rating = it) }
            }
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (expanded) Int.MAX_VALUE else COLLAPSED_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { result -> if (!expanded) overflowing = result.hasVisualOverflow },
            )
            if (overflowing || expanded) {
                TextButton(onClick = { expanded = !expanded }, contentPadding = PaddingValues(0.dp)) {
                    Text(stringResource(if (expanded) R.string.review_show_less else R.string.review_read_more))
                }
            }
        }
    }
}

@Composable
private fun Avatar(
    name: String,
    url: String?,
) {
    Box(
        modifier =
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name.firstOrNull()?.uppercase().orEmpty(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
