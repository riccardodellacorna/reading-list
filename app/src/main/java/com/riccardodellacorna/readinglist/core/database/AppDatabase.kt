package com.riccardodellacorna.readinglist.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.riccardodellacorna.readinglist.library.data.local.BookDao
import com.riccardodellacorna.readinglist.library.data.local.BookEntity

@Database(
    entities = [BookEntity::class],
    version = 1,
    exportSchema = true
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}
