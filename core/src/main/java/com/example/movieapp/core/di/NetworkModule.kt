package com.example.movieapp.core.di

import com.example.movieapp.core.BuildConfig
import com.example.movieapp.core.data.source.remote.network.ApiService
import com.example.movieapp.core.data.source.remote.network.TmdbJson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://api.themoviedb.org/3/"
private const val TIMEOUT_SECONDS = 30L

val networkModule =
    module {
        single {
            OkHttpClient
                .Builder()
                .addInterceptor { chain ->
                    val request =
                        chain
                            .request()
                            .newBuilder()
                            .header("Authorization", "Bearer ${BuildConfig.TMDB_API_TOKEN}")
                            .header("Accept", "application/json")
                            .build()
                    chain.proceed(request)
                }.apply {
                    if (BuildConfig.DEBUG) {
                        addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                    }
                }.connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()
        }
        single<ApiService> {
            Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .client(get())
                .addConverterFactory(TmdbJson.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(ApiService::class.java)
        }
    }
