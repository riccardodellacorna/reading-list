package com.riccardodellacorna.readinglist.library.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.riccardodellacorna.readinglist.core.model.ReadingStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM books ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getById(id: String): BookEntity?

    /** @Upsert inserisce o aggiorna se la chiave esiste già.
     * Le query con Flow si riemettono da sole a ogni modifica della tabella.**/
    @Upsert
    suspend fun upsert(book: BookEntity)

    @Query("UPDATE books SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: ReadingStatus)

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteById(id: String)
}
