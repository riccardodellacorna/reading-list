package com.riccardodellacorna.readinglist.core.di

import com.riccardodellacorna.readinglist.core.network.createJson
import com.riccardodellacorna.readinglist.core.network.createOkHttpClient
import com.riccardodellacorna.readinglist.core.network.createOpenLibraryApi
import com.riccardodellacorna.readinglist.search.data.remote.OpenLibraryApi
import org.koin.dsl.module

val networkModule = module {
    single { createJson() }
    single { createOkHttpClient() }
    single<OpenLibraryApi> {
        createOpenLibraryApi(
            baseUrl = "https://openlibrary.org/",
            client = get(),
            json = get()
        )
    }
}
