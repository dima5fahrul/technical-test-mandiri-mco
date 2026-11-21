package com.example.movieapp.core.utils

import com.example.movieapp.core.data.source.remote.response.AuthorDetailsResponse
import com.example.movieapp.core.data.source.remote.response.GenreResponse
import com.example.movieapp.core.data.source.remote.response.MovieDetailResponse
import com.example.movieapp.core.data.source.remote.response.MovieResponse
import com.example.movieapp.core.data.source.remote.response.ReviewResponse
import com.example.movieapp.core.data.source.remote.response.VideoResponse
import com.example.movieapp.core.domain.model.Genre
import com.example.movieapp.core.domain.model.Movie
import com.example.movieapp.core.domain.model.Trailer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DataMapperTest {
    @Test
    fun `maps genre response to domain`() {
        assertEquals(Genre(28, "Action"), DataMapper.mapGenre(GenreResponse(28, "Action")))
    }

    @Test
    fun `maps movie with full poster url`() {
        val response = MovieResponse(1, "Dune", "/dune.jpg", "2021-09-15", 7.8)

        val movie = DataMapper.mapMovie(response)

        assertEquals(Movie(1, "Dune", "https://image.tmdb.org/t/p/w500/dune.jpg", "2021-09-15", 7.8), movie)
    }

    @Test
    fun `movie without poster or release date keeps them null`() {
        val movie = DataMapper.mapMovie(MovieResponse(id = 2, title = "Untitled", posterPath = null, releaseDate = ""))

        assertNull(movie.posterUrl)
        assertNull(movie.releaseDate)
    }

    @Test
    fun `maps movie detail with genres and image urls`() {
        val response =
            MovieDetailResponse(
                id = 10,
                title = "Arrival",
                tagline = "Why are they here?",
                overview = "A linguist works with the military.",
                posterPath = "/p.jpg",
                backdropPath = "/b.jpg",
                releaseDate = "2016-11-10",
                runtime = 116,
                voteAverage = 7.6,
                voteCount = 18000,
                genres = listOf(GenreResponse(18, "Drama"), GenreResponse(878, "Science Fiction")),
                status = "Released",
            )

        val detail = DataMapper.mapMovieDetail(response)

        assertEquals("https://image.tmdb.org/t/p/w500/p.jpg", detail.posterUrl)
        assertEquals("https://image.tmdb.org/t/p/w780/b.jpg", detail.backdropUrl)
        assertEquals(listOf(Genre(18, "Drama"), Genre(878, "Science Fiction")), detail.genres)
        assertEquals(116, detail.runtimeMinutes)
        assertEquals("Why are they here?", detail.tagline)
    }

    @Test
    fun `movie detail with blank optional fields maps them to null or empty`() {
        val detail = DataMapper.mapMovieDetail(MovieDetailResponse(id = 11, tagline = "", overview = null, runtime = 0))

        assertNull(detail.tagline)
        assertEquals("", detail.overview)
        assertNull(detail.runtimeMinutes)
        assertNull(detail.posterUrl)
        assertNull(detail.backdropUrl)
    }

    @Test
    fun `maps review with tmdb hosted avatar and rating`() {
        val response =
            ReviewResponse(
                id = "r1",
                author = "jane",
                authorDetails = AuthorDetailsResponse(avatarPath = "/avatar.jpg", rating = 8.0),
                content = "Great movie",
                createdAt = "2024-01-02T10:00:00.000Z",
            )

        val review = DataMapper.mapReview(response)

        assertEquals("jane", review.author)
        assertEquals("https://image.tmdb.org/t/p/w185/avatar.jpg", review.avatarUrl)
        assertEquals(8.0, review.rating)
        assertEquals("Great movie", review.content)
    }

    @Test
    fun `review avatar pointing to external url drops the leading slash`() {
        val response =
            ReviewResponse(
                id = "r2",
                author = "john",
                authorDetails = AuthorDetailsResponse(avatarPath = "/https://www.gravatar.com/avatar/abc.jpg"),
            )

        assertEquals("https://www.gravatar.com/avatar/abc.jpg", DataMapper.mapReview(response).avatarUrl)
    }

    @Test
    fun `review content is cleaned to plain text`() {
        val review = DataMapper.mapReview(ReviewResponse(id = "r4", author = "jane", content = "**Great** movie\r\n"))

        assertEquals("Great movie", review.content)
    }

    @Test
    fun `review without author details has no avatar or rating`() {
        val review = DataMapper.mapReview(ReviewResponse(id = "r3", author = "anon", authorDetails = null))

        assertNull(review.avatarUrl)
        assertNull(review.rating)
    }

    @Test
    fun `picks official youtube trailer over teaser and unofficial trailer`() {
        val videos =
            listOf(
                VideoResponse(key = "teaser", name = "Teaser", site = "YouTube", type = "Teaser", official = true),
                VideoResponse(key = "fan", name = "Fan Trailer", site = "YouTube", type = "Trailer", official = false),
                VideoResponse(key = "vimeo", name = "Vimeo Trailer", site = "Vimeo", type = "Trailer", official = true),
                VideoResponse(
                    key = "main",
                    name = "Official Trailer",
                    site = "YouTube",
                    type = "Trailer",
                    official = true,
                ),
            )

        assertEquals(Trailer("main", "Official Trailer"), DataMapper.pickTrailer(videos))
    }

    @Test
    fun `falls back to teaser when there is no trailer`() {
        val videos =
            listOf(
                VideoResponse(key = "clip", name = "Clip", site = "YouTube", type = "Clip"),
                VideoResponse(key = "teaser", name = "Teaser", site = "YouTube", type = "Teaser"),
            )

        assertEquals(Trailer("teaser", "Teaser"), DataMapper.pickTrailer(videos))
    }

    @Test
    fun `returns null when no youtube video is available`() {
        val videos = listOf(VideoResponse(key = "v", name = "Trailer", site = "Vimeo", type = "Trailer"))

        assertNull(DataMapper.pickTrailer(videos))
        assertNull(DataMapper.pickTrailer(emptyList()))
    }
}
