package com.riccardodellacorna.readinglist.core.database

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.riccardodellacorna.readinglist.core.model.ReadingStatus
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    private val insertV1Book = """
        INSERT INTO books (id, title, authors, firstPublishYear, coverUrl, status, addedAt)
        VALUES ('OL1W', 'Dune', '["Frank Herbert"]', 1965, NULL, 'READING', 1000)
    """.trimIndent()

    @Test
    fun migrate1To2_keepsExistingRows_andRatingIsNull() {
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL(insertV1Book)
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 2, true)

        db.query("SELECT * FROM books WHERE id = 'OL1W'").use { cursor ->
            assertThat(cursor.moveToFirst()).isTrue()
            assertThat(cursor.getString(cursor.getColumnIndexOrThrow("title"))).isEqualTo("Dune")
            assertThat(cursor.getString(cursor.getColumnIndexOrThrow("status"))).isEqualTo("READING")
            assertThat(cursor.isNull(cursor.getColumnIndexOrThrow("rating"))).isTrue()
        }
    }

    @Test
    fun migratedDatabase_isReadableThroughRoomAndDao() = runBlocking {
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL(insertV1Book)
            close()
        }

        val db = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
            TEST_DB
        ).build()
        helper.closeWhenFinished(db)

        val book = db.bookDao().getById("OL1W")

        assertThat(book).isNotNull()
        assertThat(book!!.authors).containsExactly("Frank Herbert")
        assertThat(book.status).isEqualTo(ReadingStatus.READING)
        assertThat(book.rating).isNull()
    }

    private companion object {
        const val TEST_DB = "migration-test"
    }
}
