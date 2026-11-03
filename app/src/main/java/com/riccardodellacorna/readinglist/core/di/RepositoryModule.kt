package com.riccardodellacorna.readinglist.core.di

import com.riccardodellacorna.readinglist.library.data.LibraryRepository
import com.riccardodellacorna.readinglist.library.domain.ILibraryRepository
import com.riccardodellacorna.readinglist.search.data.SearchRepository
import com.riccardodellacorna.readinglist.search.domain.ISearchRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<ISearchRepository> {
        SearchRepository(
            api = get(),
            dispatchers = get()
        )
    }
    single<ILibraryRepository> {
        LibraryRepository(
            dao = get(),
            clock = get()
        )
    }
}
