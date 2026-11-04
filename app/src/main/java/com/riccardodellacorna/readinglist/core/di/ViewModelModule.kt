package com.riccardodellacorna.readinglist.core.di

import com.riccardodellacorna.readinglist.library.LibraryViewModel
import com.riccardodellacorna.readinglist.search.SearchViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::SearchViewModel)
    viewModelOf(::LibraryViewModel)
}
