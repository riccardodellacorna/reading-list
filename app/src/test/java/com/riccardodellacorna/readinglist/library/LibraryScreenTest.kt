package com.riccardodellacorna.readinglist.library

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import com.riccardodellacorna.readinglist.TestApplication
import com.riccardodellacorna.readinglist.core.model.Book
import com.riccardodellacorna.readinglist.core.model.ReadingStatus
import com.riccardodellacorna.readinglist.core.model.SavedBook
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [34],
    application = TestApplication::class,
)
class LibraryScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val dune = SavedBook(
        book = Book(
            "OL1W",
            "Dune",
            listOf("Frank Herbert"),
            1965,
            null,
        ),
        status = ReadingStatus.TO_READ,
        addedAt = 0L,
    )

    private fun show(
        state: LibraryUiState,
        onFilterChange: (ReadingStatus?) -> Unit = {},
        onStatusChange: (String, ReadingStatus) -> Unit = { _, _ -> },
        onRemove: (String) -> Unit = {},
    ) {
        composeRule.setContent {
            LibraryScreen(
                state,
                onFilterChange,
                onStatusChange,
                onRemove,
            )
        }
    }

    @Test
    fun empty_library_shows_a_message() {
        show(LibraryUiState(isLoading = false))

        composeRule.onNodeWithText("Your library is empty. Search for a book to add it.")
            .assertIsDisplayed()
    }

    @Test
    fun books_are_listed_with_their_status() {
        show(
            LibraryUiState(
                books = listOf(dune),
                isLoading = false,
            ),
        )

        composeRule.onNodeWithText("Dune").assertIsDisplayed()
        composeRule.onNodeWithTag("status_OL1W").assertIsDisplayed()
    }

    @Test
    fun clicking_a_filter_chip_notifies_the_filter() {
        var received: ReadingStatus? = null
        show(
            LibraryUiState(isLoading = false),
            onFilterChange = { received = it },
        )

        composeRule.onNodeWithTag("filter_READING").performClick()

        assertThat(received).isEqualTo(ReadingStatus.READING)
    }

    @Test
    fun choosing_a_status_from_the_menu_notifies_the_change() {
        var changed: Pair<String, ReadingStatus>? = null
        show(
            LibraryUiState(
                books = listOf(dune),
                isLoading = false,
            ),
            onStatusChange = { id, status -> changed = id to status },
        )

        composeRule.onNodeWithTag("status_OL1W").performClick()
        composeRule.onNodeWithTag("status_option_READ").performClick()

        assertThat(changed).isEqualTo("OL1W" to ReadingStatus.READ)
    }

    @Test
    fun clicking_delete_removes_the_book() {
        var removed: String? = null
        show(
            LibraryUiState(
                books = listOf(dune),
                isLoading = false,
            ),
            onRemove = { removed = it },
        )

        composeRule.onNodeWithContentDescription("Remove Dune").performClick()

        assertThat(removed).isEqualTo("OL1W")
    }
}
