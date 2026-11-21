package com.example.movieapp.presentation.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

@Composable
fun <T : Any> PagedContent(
    items: LazyPagingItems<T>,
    @StringRes emptyMessage: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val refresh = items.loadState.refresh
    val isEmpty = items.itemCount == 0
    when {
        isEmpty && refresh is LoadState.Loading -> LoadingContent(modifier)
        isEmpty && refresh is LoadState.Error ->
            ErrorContent(error = refresh.error.asAppError(), onRetry = items::retry, modifier = modifier)
        isEmpty && refresh is LoadState.NotLoading && items.loadState.append.endOfPaginationReached ->
            EmptyContent(message = emptyMessage, modifier = modifier)
        else -> content()
    }
}
