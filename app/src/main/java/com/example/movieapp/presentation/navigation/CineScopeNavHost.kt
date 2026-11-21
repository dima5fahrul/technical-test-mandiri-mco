package com.example.movieapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.movieapp.presentation.detail.MovieDetailScreen
import com.example.movieapp.presentation.genre.GenreScreen
import com.example.movieapp.presentation.movies.MovieListScreen
import com.example.movieapp.presentation.review.ReviewScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CineScopeNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = GenreRoute) {
        composable<GenreRoute> { entry ->
            GenreScreen(
                onGenreClick = { genre ->
                    if (entry.isResumed()) navController.navigate(MovieListRoute(genre.id, genre.name))
                },
            )
        }
        composable<MovieListRoute> { entry ->
            val route = entry.toRoute<MovieListRoute>()
            MovieListScreen(
                genreName = route.genreName,
                onBack = navController::navigateUp,
                onMovieClick = { movie ->
                    if (entry.isResumed()) navController.navigate(MovieDetailRoute(movie.id))
                },
                viewModel = koinViewModel(parameters = { parametersOf(route.genreId) }),
            )
        }
        composable<MovieDetailRoute> { entry ->
            val route = entry.toRoute<MovieDetailRoute>()
            MovieDetailScreen(
                onBack = navController::navigateUp,
                onReviewsClick = { detail ->
                    if (entry.isResumed()) navController.navigate(ReviewRoute(detail.id, detail.title))
                },
                viewModel = koinViewModel(parameters = { parametersOf(route.movieId) }),
            )
        }
        composable<ReviewRoute> { entry ->
            val route = entry.toRoute<ReviewRoute>()
            ReviewScreen(
                movieTitle = route.movieTitle,
                onBack = navController::navigateUp,
                viewModel = koinViewModel(parameters = { parametersOf(route.movieId) }),
            )
        }
    }
}

private fun NavBackStackEntry.isResumed() = lifecycle.currentState == Lifecycle.State.RESUMED
