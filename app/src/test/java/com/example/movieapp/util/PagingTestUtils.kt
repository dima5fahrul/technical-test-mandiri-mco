package com.example.movieapp.util

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.testing.asPagingSourceFactory
import kotlinx.coroutines.flow.Flow

fun <T : Any> List<T>.asPagingFlow(): Flow<PagingData<T>> {
    val sourceFactory = asPagingSourceFactory()
    return Pager(PagingConfig(pageSize = 20)) { sourceFactory() }.flow
}
