package com.riccardodellacorna.readinglist.core.model

data class SavedBook(
    val book: Book,
    val status: ReadingStatus,
    val addedAt: Long,
)

enum class ReadingStatus {
    TO_READ,
    READING,
    READ
}
