package com.example.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.movieapp.presentation.navigation.TechnicalTestMovieAppNavHost
import com.example.movieapp.presentation.theme.TechnicalTestMovieAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TechnicalTestMovieAppTheme {
                TechnicalTestMovieAppNavHost()
            }
        }
    }
}
