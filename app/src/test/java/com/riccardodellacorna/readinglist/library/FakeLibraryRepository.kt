package com.riccardodellacorna.readinglist.library

import com.riccardodellacorna.readinglist.core.model.Book
import com.riccardodellacorna.readinglist.core.model.ReadingStatus
import com.riccardodellacorna.readinglist.core.model.SavedBook
import com.riccardodellacorna.readinglist.library.domain.ILibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeLibraryRepository(initial: List<SavedBook> = emptyList()) : ILibraryRepository {

    private val books = MutableStateFlow(initial)

    override fun observeLibrary(): Flow<List<SavedBook>> = books

    override suspend fun save(book: Book) {
        books.update { list ->
            if (list.any { it.book.id == book.id }) list
            else list + SavedBook(
                book,
                ReadingStatus.TO_READ,
                addedAt = 0L
            )
        }
    }

    override suspend fun updateStatus(
        id: String,
        status: ReadingStatus,
    ) {
        books.update { list -> list.map { if (it.book.id == id) it.copy(status = status) else it } }
    }

    override suspend fun remove(id: String) {
        books.update { list -> list.filterNot { it.book.id == id } }
    }
}
