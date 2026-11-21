package com.example.movieapp.core.utils

import com.example.movieapp.core.data.source.remote.response.GenreResponse
import com.example.movieapp.core.data.source.remote.response.MovieDetailResponse
import com.example.movieapp.core.data.source.remote.response.MovieResponse
import com.example.movieapp.core.data.source.remote.response.ReviewResponse
import com.example.movieapp.core.data.source.remote.response.VideoResponse
import com.example.movieapp.core.domain.model.Genre
import com.example.movieapp.core.domain.model.Movie
import com.example.movieapp.core.domain.model.MovieDetail
import com.example.movieapp.core.domain.model.Review
import com.example.movieapp.core.domain.model.Trailer

object DataMapper {
    private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
    private const val POSTER_SIZE = "w500"
    private const val BACKDROP_SIZE = "w780"
    private const val AVATAR_SIZE = "w185"

    private val videoTypeRank = mapOf("Trailer" to 0, "Teaser" to 1)

    fun mapGenre(response: GenreResponse): Genre = Genre(id = response.id, name = response.name)

    fun mapMovie(response: MovieResponse): Movie =
        Movie(
            id = response.id,
            title = response.title,
            posterUrl = imageUrl(response.posterPath, POSTER_SIZE),
            releaseDate = response.releaseDate?.takeIf { it.isNotBlank() },
            rating = response.voteAverage,
        )

    fun mapMovieDetail(response: MovieDetailResponse): MovieDetail =
        MovieDetail(
            id = response.id,
            title = response.title,
            tagline = response.tagline?.takeIf { it.isNotBlank() },
            overview = response.overview.orEmpty(),
            posterUrl = imageUrl(response.posterPath, POSTER_SIZE),
            backdropUrl = imageUrl(response.backdropPath, BACKDROP_SIZE),
            releaseDate = response.releaseDate?.takeIf { it.isNotBlank() },
            runtimeMinutes = response.runtime?.takeIf { it > 0 },
            rating = response.voteAverage,
            voteCount = response.voteCount,
            genres = response.genres.map(::mapGenre),
            status = response.status?.takeIf { it.isNotBlank() },
        )

    fun mapReview(response: ReviewResponse): Review =
        Review(
            id = response.id,
            author = response.author,
            avatarUrl = avatarUrl(response.authorDetails?.avatarPath),
            rating = response.authorDetails?.rating,
            content = ReviewTextCleaner.clean(response.content),
            createdAt = response.createdAt,
        )

    fun pickTrailer(videos: List<VideoResponse>): Trailer? =
        videos
            .filter { it.site.equals("YouTube", ignoreCase = true) && it.key.isNotBlank() }
            .sortedWith(
                compareBy<VideoResponse> { videoTypeRank[it.type] ?: videoTypeRank.size }
                    .thenByDescending { it.official },
            ).firstOrNull()
            ?.let { Trailer(key = it.key, name = it.name) }

    private fun imageUrl(
        path: String?,
        size: String,
    ): String? = path?.takeIf { it.isNotBlank() }?.let { "$IMAGE_BASE_URL$size$it" }

    private fun avatarUrl(path: String?): String? =
        when {
            path.isNullOrBlank() -> null
            path.startsWith("/http") -> path.removePrefix("/")
            else -> imageUrl(path, AVATAR_SIZE)
        }
}
