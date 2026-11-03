package com.riccardodellacorna.readinglist.library.data.local

import com.google.common.truth.Truth.assertThat
import com.riccardodellacorna.readinglist.core.model.Book
import com.riccardodellacorna.readinglist.core.model.ReadingStatus
import org.junit.Test

class BookEntityMapperTest {

    private val book = Book(
        id = "OL1W",
        title = "Dune",
        authors = listOf("Frank Herbert"),
        firstPublishYear = 1965,
        coverUrl = "https://example.com/cover.jpg"
    )

    @Test
    fun `book to entity and back keeps all data`() {
        val entity = book.toEntity(status = ReadingStatus.READING, addedAt = 42L)

        val saved = entity.toDomain()

        assertThat(saved.book).isEqualTo(book)
        assertThat(saved.status).isEqualTo(ReadingStatus.READING)
        assertThat(saved.addedAt).isEqualTo(42L)
    }

    @Test
    fun `new books default to TO_READ`() {
        assertThat(book.toEntity(addedAt = 1L).status).isEqualTo(ReadingStatus.TO_READ)
    }
}
