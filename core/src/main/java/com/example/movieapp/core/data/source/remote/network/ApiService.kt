package com.example.movieapp.core.data.source.remote.network

import com.example.movieapp.core.data.source.remote.response.GenreListResponse
import com.example.movieapp.core.data.source.remote.response.MovieDetailResponse
import com.example.movieapp.core.data.source.remote.response.MovieResponse
import com.example.movieapp.core.data.source.remote.response.PagedResponse
import com.example.movieapp.core.data.source.remote.response.ReviewResponse
import com.example.movieapp.core.data.source.remote.response.VideoListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("genre/movie/list")
    suspend fun getGenres(): GenreListResponse

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("include_adult") includeAdult: Boolean = false,
    ): PagedResponse<MovieResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: Int,
    ): MovieDetailResponse

    @GET("movie/{movie_id}/reviews")
    suspend fun getReviews(
        @Path("movie_id") movieId: Int,
        @Query("page") page: Int,
    ): PagedResponse<ReviewResponse>

    @GET("movie/{movie_id}/videos")
    suspend fun getVideos(
        @Path("movie_id") movieId: Int,
    ): VideoListResponse
}
