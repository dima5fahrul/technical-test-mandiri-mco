package com.example.movieapp.core.data.source.remote.response

import com.example.movieapp.core.data.source.remote.network.TmdbJson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ResponseParsingTest {
    @Test
    fun `discover page tolerates null fields and unknown keys`() {
        val json =
            """
            {
              "page": 1,
              "results": [
                {"id": 1, "title": "Dune", "poster_path": null, "release_date": "2021-09-15",
                 "vote_average": null, "adult": false, "genre_ids": [878]}
              ],
              "total_pages": 42,
              "total_results": 830
            }
            """.trimIndent()

        val response = TmdbJson.decodeFromString<PagedResponse<MovieResponse>>(json)

        assertEquals(42, response.totalPages)
        assertNull(response.results.single().posterPath)
        assertEquals(0.0, response.results.single().voteAverage, 0.0)
    }

    @Test
    fun `movie detail parses nested genres and null runtime`() {
        val json =
            """
            {"id": 603, "title": "The Matrix", "runtime": null, "tagline": null,
             "genres": [{"id": 28, "name": "Action"}], "belongs_to_collection": {"id": 2344}}
            """.trimIndent()

        val response = TmdbJson.decodeFromString<MovieDetailResponse>(json)

        assertNull(response.runtime)
        assertEquals(listOf(GenreResponse(28, "Action")), response.genres)
    }

    @Test
    fun `review parses author details with null rating`() {
        val json =
            """
            {"page": 1, "total_pages": 1, "total_results": 1, "results": [
              {"id": "abc", "author": "jane", "content": "Nice",
               "author_details": {"name": "", "username": "jane", "avatar_path": null, "rating": null},
               "created_at": "2024-01-02T10:00:00.000Z"}
            ]}
            """.trimIndent()

        val review = TmdbJson.decodeFromString<PagedResponse<ReviewResponse>>(json).results.single()

        assertEquals("jane", review.author)
        assertNull(review.authorDetails?.rating)
    }
}
