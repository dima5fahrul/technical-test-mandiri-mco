package com.example.movieapp.core.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadResult
import androidx.paging.testing.TestPager
import com.example.movieapp.core.data.source.remote.response.PagedResponse
import com.example.movieapp.core.domain.model.AppError
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class TmdbPagingSourceTest {
    private val config = PagingConfig(pageSize = 20, enablePlaceholders = false)

    private fun pagedSource(totalPages: Int) =
        TmdbPagingSource { page ->
            PagedResponse(page = page, results = listOf("item-$page"), totalPages = totalPages)
        }

    @Test
    fun `first page has no previous key and points to page 2`() =
        runTest {
            val result = TestPager(config, pagedSource(totalPages = 3)).refresh() as LoadResult.Page

            assertEquals(listOf("item-1"), result.data)
            assertNull(result.prevKey)
            assertEquals(2, result.nextKey)
        }

    @Test
    fun `append loads the following page`() =
        runTest {
            val pager = TestPager(config, pagedSource(totalPages = 3))
            pager.refresh()

            val result = pager.append() as LoadResult.Page

            assertEquals(listOf("item-2"), result.data)
            assertEquals(1, result.prevKey)
            assertEquals(3, result.nextKey)
        }

    @Test
    fun `last page ends pagination`() =
        runTest {
            val result = TestPager(config, pagedSource(totalPages = 1)).refresh() as LoadResult.Page

            assertNull(result.nextKey)
        }

    @Test
    fun `pagination stops at tmdb page limit even if more pages are reported`() =
        runTest {
            val pager = TestPager(config, pagedSource(totalPages = 1_000))

            val result = pager.refresh(initialKey = TmdbPagingSource.MAX_PAGE) as LoadResult.Page

            assertNull(result.nextKey)
        }

    @Test
    fun `empty page ends pagination`() =
        runTest {
            val source = TmdbPagingSource { PagedResponse<String>(page = 1, results = emptyList(), totalPages = 5) }

            val result = TestPager(config, source).refresh() as LoadResult.Page

            assertTrue(result.data.isEmpty())
            assertNull(result.nextKey)
        }

    @Test
    fun `failure is reported as AppError`() =
        runTest {
            val source = TmdbPagingSource<String> { throw IOException("offline") }

            val result = TestPager(config, source).refresh() as LoadResult.Error

            assertEquals(AppError.NoInternet, result.throwable)
        }
}
