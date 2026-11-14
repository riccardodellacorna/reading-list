package com.riccardodellacorna.readinglist.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.riccardodellacorna.readinglist.library.data.local.BookDao
import com.riccardodellacorna.readinglist.library.data.local.BookEntity

@Database(
    entities = [BookEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}
