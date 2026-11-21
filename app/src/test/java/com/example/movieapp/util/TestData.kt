package com.example.movieapp.util

import com.example.movieapp.core.domain.model.Genre
import com.example.movieapp.core.domain.model.Movie
import com.example.movieapp.core.domain.model.MovieDetail
import com.example.movieapp.core.domain.model.Review
import com.example.movieapp.core.domain.model.Trailer

object TestData {
    val genres = listOf(Genre(28, "Action"), Genre(35, "Comedy"))

    val movies =
        listOf(
            Movie(1, "Dune", "https://image.tmdb.org/t/p/w500/dune.jpg", "2021-09-15", 7.8),
            Movie(2, "Arrival", null, null, 7.6),
        )

    val movieDetail =
        MovieDetail(
            id = 603,
            title = "The Matrix",
            tagline = "Welcome to the Real World.",
            overview = "A hacker learns the truth about reality.",
            posterUrl = null,
            backdropUrl = null,
            releaseDate = "1999-03-30",
            runtimeMinutes = 136,
            rating = 8.2,
            voteCount = 25000,
            genres = listOf(Genre(28, "Action")),
            status = "Released",
        )

    val trailer = Trailer("vKQi3bBA1y8", "Official Trailer")

    val reviews =
        listOf(
            Review("r1", "jane", null, 9.0, "Mind-bending.", "2024-01-02T10:00:00.000Z"),
            Review("r2", "john", null, null, "Classic.", null),
        )
}
