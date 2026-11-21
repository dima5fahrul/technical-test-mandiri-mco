package com.example.movieapp.core.data.source.remote.network

import kotlinx.serialization.json.Json

val TmdbJson =
    Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        coerceInputValues = true
    }
