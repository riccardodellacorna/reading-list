package com.riccardodellacorna.readinglist.library.domain

import com.riccardodellacorna.readinglist.core.model.Book
import com.riccardodellacorna.readinglist.core.model.ReadingStatus
import com.riccardodellacorna.readinglist.core.model.SavedBook
import kotlinx.coroutines.flow.Flow

interface ILibraryRepository {
    fun observeLibrary(): Flow<List<SavedBook>>
    suspend fun save(book: Book)
    suspend fun updateStatus(id: String, status: ReadingStatus)
    suspend fun remove(id: String)
}
