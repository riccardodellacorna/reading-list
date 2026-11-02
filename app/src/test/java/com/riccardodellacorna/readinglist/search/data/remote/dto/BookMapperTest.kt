package com.riccardodellacorna.readinglist.search.data.remote.dto

import com.google.common.truth.Truth.assertThat
import org.junit.Test


/**
 * La convenzione è given / when / then (preparo, eseguo, verifico) e un test per comportamento,
 * con nomi in backtick che si leggono come una frase.
 */

class BookMapperTest {

    @Test
    fun `maps all fields of a complete dto`() {
        val dto = BookDto(
            key = "/works/OL893415W",
            title = "Dune",
            authorNames = listOf("Frank Herbert"),
            firstPublishYear = 1965,
            coverId = 12345
        )

        val book = dto.toDomain()

        assertThat(book).isNotNull()
        assertThat(book!!.id).isEqualTo("OL893415W")
        assertThat(book.title).isEqualTo("Dune")
        assertThat(book.authors).containsExactly("Frank Herbert")
        assertThat(book.firstPublishYear).isEqualTo(1965)
        assertThat(book.coverUrl).isEqualTo("https://covers.openlibrary.org/b/id/12345-M.jpg")
    }

    @Test
    fun `missing authors become an empty list`() {
        val dto = BookDto(
            key = "/works/OL1W",
            title = "Anonymous",
            authorNames = null
        )

        assertThat(dto.toDomain()!!.authors).isEmpty()
    }

    @Test
    fun `missing cover id gives null cover url`() {
        val dto = BookDto(
            key = "/works/OL1W",
            title = "No cover",
            coverId = null
        )

        assertThat(dto.toDomain()!!.coverUrl).isNull()
    }

    @Test
    fun `blank title is discarded`() {
        val dto = BookDto(
            key = "/works/OL1W",
            title = "   "
        )

        assertThat(dto.toDomain()).isNull()
    }

    @Test
    fun `list mapping skips invalid items`() {
        val dtos = listOf(
            BookDto(
                key = "/works/OL1W",
                title = "Valid"
            ),
            BookDto(
                key = "/works/OL2W",
                title = null
            )
        )

        val books = dtos.toDomain()

        assertThat(books).hasSize(1)
        assertThat(books.first().title).isEqualTo("Valid")
    }
}
