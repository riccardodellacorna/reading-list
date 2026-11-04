package com.riccardodellacorna.readinglist.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.google.common.truth.Truth.assertThat
import com.riccardodellacorna.readinglist.TestApplication
import com.riccardodellacorna.readinglist.core.common.DataError
import com.riccardodellacorna.readinglist.core.model.Book
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
class SearchScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val dune = Book(
        "OL1W",
        "Dune",
        listOf("Frank Herbert"),
        1965,
        null,
    )

    private fun show(
        state: SearchUiState,
        onQueryChange: (String) -> Unit = {},
        onSaveClick: (Book) -> Unit = {},
    ) {
        composeRule.setContent {
            SearchScreen(
                state,
                onQueryChange,
                onSaveClick,
            )
        }
    }

    private fun results(isSaved: Boolean) = SearchUiState(
        query = "dune",
        content = SearchContent.Results(
            listOf(
                SearchItem(
                    dune,
                    isSaved,
                ),
            ),
        ),
    )

    @Test
    fun idle_state_shows_the_hint() {
        show(SearchUiState())

        composeRule.onNodeWithText("Type a title or an author").assertIsDisplayed()
    }

    @Test
    fun loading_state_shows_the_progress_indicator() {
        show(SearchUiState(content = SearchContent.Loading))

        composeRule.onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    fun results_show_title_and_authors() {
        show(results(isSaved = false))

        composeRule.onNodeWithText("Dune").assertIsDisplayed()
        composeRule.onNodeWithText("Frank Herbert").assertIsDisplayed()
    }

    @Test
    fun network_error_shows_a_readable_message() {
        show(SearchUiState(content = SearchContent.Error(DataError.Network)))

        composeRule.onNodeWithText("No connection. Check your network and try again.")
            .assertIsDisplayed()
    }

    @Test
    fun typing_notifies_the_query_change() {
        var received: String? = null
        show(
            SearchUiState(),
            onQueryChange = { received = it },
        )

        composeRule.onNodeWithTag("search_field").performTextInput("dune")

        assertThat(received).isEqualTo("dune")
    }

    @Test
    fun clicking_add_saves_the_book() {
        var saved: Book? = null
        show(
            results(isSaved = false),
            onSaveClick = { saved = it },
        )

        composeRule.onNodeWithContentDescription("Save Dune").performClick()

        assertThat(saved).isEqualTo(dune)
    }

    @Test
    fun already_saved_book_has_no_add_button() {
        show(results(isSaved = true))

        composeRule.onNodeWithContentDescription("Saved").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Save Dune").assertDoesNotExist()
    }
}
