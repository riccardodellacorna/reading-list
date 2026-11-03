package com.riccardodellacorna.readinglist.library.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.riccardodellacorna.readinglist.core.model.ReadingStatus

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val authors: List<String>,
    val firstPublishYear: Int?,
    val coverUrl: String?,
    val status: ReadingStatus,
    val addedAt: Long
)
