package com.riccardodellacorna.readinglist.core.model

data class SavedBook(
    val book: Book,
    val status: ReadingStatus,
    val addedAt: Long,
    val rating: Int? = null
)

enum class ReadingStatus {
    TO_READ,
    READING,
    READ
}
