package com.riccardodellacorna.readinglist.core.di

import org.koin.core.module.Module

val appModules = listOf<Module>(
    commonModule,
    networkModule,
    databaseModule,
    repositoryModule,
    viewModelModule
)
