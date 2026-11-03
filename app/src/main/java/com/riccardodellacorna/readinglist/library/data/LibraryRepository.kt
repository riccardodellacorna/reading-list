package com.riccardodellacorna.readinglist.library.data

import com.riccardodellacorna.readinglist.core.common.Clock
import com.riccardodellacorna.readinglist.core.model.Book
import com.riccardodellacorna.readinglist.core.model.ReadingStatus
import com.riccardodellacorna.readinglist.core.model.SavedBook
import com.riccardodellacorna.readinglist.library.data.local.BookDao
import com.riccardodellacorna.readinglist.library.data.local.toDomain
import com.riccardodellacorna.readinglist.library.data.local.toEntity
import com.riccardodellacorna.readinglist.library.domain.ILibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LibraryRepository(
    private val dao: BookDao,
    private val clock: Clock,
) : ILibraryRepository {

    override fun observeLibrary(): Flow<List<SavedBook>> = dao.observeAll().map { entities ->
        entities.map {
            it.toDomain()
        }
    }

    override suspend fun save(
        book: Book,
    ) {
        if (dao.getById(book.id) != null) {
            return
        }
        dao.upsert(
            book.toEntity(
                addedAt = clock.nowMillis()
            )
        )
    }

    override suspend fun updateStatus(
        id: String,
        status: ReadingStatus,
    ) = dao.updateStatus(
        id,
        status
    )

    override suspend fun remove(
        id: String,
    ) = dao.deleteById(id)
}
