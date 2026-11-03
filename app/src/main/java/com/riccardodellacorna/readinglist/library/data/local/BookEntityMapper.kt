package com.riccardodellacorna.readinglist.library.data.local

import com.riccardodellacorna.readinglist.core.model.Book
import com.riccardodellacorna.readinglist.core.model.ReadingStatus
import com.riccardodellacorna.readinglist.core.model.SavedBook

fun BookEntity.toDomain(): SavedBook = SavedBook(
    book = Book(
        id = id,
        title = title,
        authors = authors,
        firstPublishYear = firstPublishYear,
        coverUrl = coverUrl
    ),
    status = status,
    addedAt = addedAt
)

fun Book.toEntity(
    status: ReadingStatus = ReadingStatus.TO_READ,
    addedAt: Long
): BookEntity = BookEntity(
    id = id,
    title = title,
    authors = authors,
    firstPublishYear = firstPublishYear,
    coverUrl = coverUrl,
    status = status,
    addedAt = addedAt
)
