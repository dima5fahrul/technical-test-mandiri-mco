package com.example.movieapp.core.data.source.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.movieapp.core.data.source.remote.network.toAppError
import com.example.movieapp.core.data.source.remote.response.PagedResponse
import kotlin.coroutines.cancellation.CancellationException

class TmdbPagingSource<T : Any>(
    private val fetchPage: suspend (page: Int) -> PagedResponse<T>,
) : PagingSource<Int, T>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: FIRST_PAGE
        return try {
            val response = fetchPage(page)
            val lastPage = minOf(response.totalPages, MAX_PAGE)
            LoadResult.Page(
                data = response.results,
                prevKey = if (page == FIRST_PAGE) null else page - 1,
                nextKey = if (response.results.isEmpty() || page >= lastPage) null else page + 1,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e.toAppError())
        }
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? =
        state.anchorPosition?.let { anchor ->
            val page = state.closestPageToPosition(anchor)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }

    companion object {
        const val FIRST_PAGE = 1

        const val MAX_PAGE = 500
    }
}
