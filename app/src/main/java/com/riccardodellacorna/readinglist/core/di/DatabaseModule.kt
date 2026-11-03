package com.riccardodellacorna.readinglist.core.di

import androidx.room.Room
import com.riccardodellacorna.readinglist.core.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "reading_list.db"
        ).build()
    }
    single { get<AppDatabase>().bookDao() }
}
