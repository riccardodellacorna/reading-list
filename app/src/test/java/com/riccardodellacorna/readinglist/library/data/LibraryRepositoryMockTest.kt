package com.riccardodellacorna.readinglist.library.data

import com.riccardodellacorna.readinglist.library.data.local.BookDao
import com.riccardodellacorna.readinglist.testutil.FakeClock
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * Lo stesso test fatto nel LibraryRepositoryTest ma con un mock, per confrontarli.
 *
 * Confronta con remove deletes the book del file LibraryRepositoryTest:
 * - Mock: verifica un’interazione (deleteById è stato chiamato). Si rompe se cambi il modo in cui il repository cancella,
 *         anche se il comportamento resta corretto.
 * - Fake: verifica un risultato (il libro non c’è più). Più robusto ai refactoring.
 * **/

class LibraryRepositoryMockTest {

    private val dao = mockk<BookDao>(relaxed = true)
    private val repository = LibraryRepository(dao, FakeClock())

    @Test
    fun `remove delegates to the dao`() = runTest {
        repository.remove("OL1W")

        coVerify(exactly = 1) { dao.deleteById("OL1W") }
    }
}
