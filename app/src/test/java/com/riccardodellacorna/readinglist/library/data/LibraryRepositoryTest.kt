package com.riccardodellacorna.readinglist.library.data

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.riccardodellacorna.readinglist.core.model.Book
import com.riccardodellacorna.readinglist.core.model.ReadingStatus
import com.riccardodellacorna.readinglist.library.data.local.FakeBookDao
import com.riccardodellacorna.readinglist.testutil.FakeClock
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * Guarda lo stesso test fatto con un mock nel LibraryRepositoryMockTest per confrontarli.
 * **/
class LibraryRepositoryTest {

    private val dao = FakeBookDao()
    private val clock = FakeClock(now = 1_000L)
    private val repository = LibraryRepository(
        dao,
        clock
    )

    private fun book(
        id: String = "OL1W",
        title: String = "Dune",
    ) = Book(
        id,
        title,
        listOf("Frank Herbert"),
        1965,
        null
    )

    @Test
    fun `saved book starts as TO_READ with the current time`() = runTest {
        repository.save(book())

        repository.observeLibrary().test {
            val saved = awaitItem().single()
            assertThat(saved.book).isEqualTo(book())
            assertThat(saved.status).isEqualTo(ReadingStatus.TO_READ)
            assertThat(saved.addedAt).isEqualTo(1_000L)
        }
    }

    @Test
    fun `saving an existing book does not reset its status`() = runTest {
        repository.save(book())
        repository.updateStatus(
            "OL1W",
            ReadingStatus.READING
        )

        repository.save(book())

        repository.observeLibrary().test {
            assertThat(awaitItem().single().status).isEqualTo(ReadingStatus.READING)
        }
    }

    @Test
    fun `library is ordered with the most recently added first`() = runTest {
        repository.save(book(id = "A"))
        clock.now = 2_000L
        repository.save(book(id = "B"))

        repository.observeLibrary().test {
            assertThat(awaitItem().map { it.book.id }).containsExactly(
                "B",
                "A"
            ).inOrder()
        }
    }

    @Test
    fun `updateStatus changes the status of the book`() = runTest {
        repository.save(book())

        repository.updateStatus(
            "OL1W",
            ReadingStatus.READ
        )

        repository.observeLibrary().test {
            assertThat(awaitItem().single().status).isEqualTo(ReadingStatus.READ)
        }
    }

    @Test
    fun `remove deletes the book`() = runTest {
        repository.save(book())

        repository.remove("OL1W")

        repository.observeLibrary().test {
            assertThat(awaitItem()).isEmpty()
        }
    }
}
