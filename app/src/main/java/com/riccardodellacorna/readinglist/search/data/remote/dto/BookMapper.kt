package com.riccardodellacorna.readinglist.search.data.remote.dto

import com.riccardodellacorna.readinglist.core.model.Book

fun BookDto.toDomain(): Book? {
    val validTitle = title?.takeIf {
        it.isNotBlank()
    } ?: return null

    return Book(
        id = key.removePrefix("/works/"),
        title = validTitle,
        authors = authorNames.orEmpty(),
        firstPublishYear = firstPublishYear,
        coverUrl = coverId?.let {
            "https://covers.openlibrary.org/b/id/$it-M.jpg"
        }
    )
}

fun List<BookDto>.toDomain(): List<Book> = mapNotNull {
    it.toDomain()
}
