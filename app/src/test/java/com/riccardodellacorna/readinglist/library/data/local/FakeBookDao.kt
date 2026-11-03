package com.riccardodellacorna.readinglist.library.data.local

import com.riccardodellacorna.readinglist.core.model.ReadingStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeBookDao : BookDao {

    private val books = MutableStateFlow<Map<String, BookEntity>>(emptyMap())

    override fun observeAll(): Flow<List<BookEntity>> = books.map {
        it.values.sortedByDescending { book -> book.addedAt }
    }

    override suspend fun getById(
        id: String,
    ): BookEntity? = books.value[id]

    override suspend fun upsert(
        book: BookEntity,
    ) {
        books.update {
            it + (book.id to book)
        }
    }

    override suspend fun updateStatus(
        id: String,
        status: ReadingStatus,
    ) {
        books.update { map ->
            map[id]?.let {
                map + (id to it.copy(status = status))
            } ?: map
        }
    }

    override suspend fun deleteById(
        id: String,
    ) {
        books.update {
            it - id
        }
    }
}