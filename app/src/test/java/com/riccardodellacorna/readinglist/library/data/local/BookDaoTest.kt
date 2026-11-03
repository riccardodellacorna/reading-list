package com.riccardodellacorna.readinglist.library.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.riccardodellacorna.readinglist.TestApplication
import com.riccardodellacorna.readinglist.core.database.AppDatabase
import com.riccardodellacorna.readinglist.core.model.ReadingStatus
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = TestApplication::class)
/**
 *  - sdk = [34] --> versione Android simulata. Il tuo compileSdk è 37 e Robolectric potrebbe non supportarla
 * ancora, quindi pinnare una versione stabile evita errori di SDK mancante al primo avvio.
 *  - application = TestApplication::class --> Application vuota al posto di ReadingListApp che usa Robolectric.
 *  Se non la creavamo,Robolectric per eseguire BookDaoTest, crea una vera istanza dell’Application
 *  del progetto, cioè ReadingListApp. Il suo onCreate() chiama startKoin { ... }, che registra Koin in un contesto globale.
 *  Dal secondo test in poi (ogni test crea una nuova Application, ma Koin globale è già avviato dal precedente) startKoin lancia
 *  KoinApplicationAlreadyStartedException.
 * **/
class BookDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: BookDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.bookDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun entity(
        id: String = "OL1W",
        title: String = "Dune",
        authors: List<String> = listOf("Frank Herbert"),
        status: ReadingStatus = ReadingStatus.TO_READ,
        addedAt: Long = 1_000L
    ) = BookEntity(
        id = id,
        title = title,
        authors = authors,
        firstPublishYear = 1965,
        coverUrl = null,
        status = status,
        addedAt = addedAt
    )

    @Test
    fun `inserted book can be read back by id`() = runTest {
        dao.upsert(entity())

        val result = dao.getById("OL1W")

        assertThat(result).isEqualTo(entity())
    }

    @Test
    fun `getById returns null when the book does not exist`() = runTest {
        assertThat(dao.getById("missing")).isNull()
    }

    @Test
    fun `authors list survives the type converter round trip`() = runTest {
        val authors = listOf("Neil Gaiman", "Terry Pratchett")
        dao.upsert(entity(authors = authors))

        assertThat(dao.getById("OL1W")!!.authors)
            .containsExactlyElementsIn(authors).inOrder()
    }

    @Test
    fun `upsert on an existing id replaces the row`() = runTest {
        dao.upsert(entity(title = "Old title"))
        dao.upsert(entity(title = "New title"))

        dao.observeAll().test {
            val books = awaitItem()
            assertThat(books).hasSize(1)
            assertThat(books.first().title).isEqualTo("New title")
        }
    }

    @Test
    fun `observeAll returns most recently added first`() = runTest {
        dao.upsert(entity(id = "A", addedAt = 1_000L))
        dao.upsert(entity(id = "B", addedAt = 3_000L))
        dao.upsert(entity(id = "C", addedAt = 2_000L))

        dao.observeAll().test {
            assertThat(awaitItem().map { it.id }).containsExactly("B", "C", "A").inOrder()
        }
    }

    @Test
    fun `observeAll emits again after a change`() = runTest {
        dao.observeAll().test {
            assertThat(awaitItem()).isEmpty()

            dao.upsert(entity())

            assertThat(awaitItem()).hasSize(1)
        }
    }

    @Test
    fun `updateStatus changes only the status`() = runTest {
        dao.upsert(entity(status = ReadingStatus.TO_READ))

        dao.updateStatus("OL1W", ReadingStatus.READ)

        val result = dao.getById("OL1W")!!
        assertThat(result.status).isEqualTo(ReadingStatus.READ)
        assertThat(result.title).isEqualTo("Dune")
    }

    @Test
    fun `deleteById removes the book`() = runTest {
        dao.upsert(entity())

        dao.deleteById("OL1W")

        assertThat(dao.getById("OL1W")).isNull()
    }
}
