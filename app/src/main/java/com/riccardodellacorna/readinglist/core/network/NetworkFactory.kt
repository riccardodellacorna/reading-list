package com.riccardodellacorna.readinglist.core.network

import com.riccardodellacorna.readinglist.search.data.remote.OpenLibraryApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

fun createJson(): Json = Json {
    ignoreUnknownKeys = true
}

fun createOkHttpClient(): OkHttpClient =
    OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
        )
        .build()

fun createOpenLibraryApi(
    baseUrl: String,
    client: OkHttpClient,
    json: Json,
): OpenLibraryApi =
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(
            json.asConverterFactory("application/json".toMediaType())
        )
        .build()
        .create(OpenLibraryApi::class.java)
