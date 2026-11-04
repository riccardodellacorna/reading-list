package com.riccardodellacorna.readinglist.library

import com.google.common.truth.Truth.assertThat
import com.riccardodellacorna.readinglist.core.model.Book
import com.riccardodellacorna.readinglist.core.model.ReadingStatus
import com.riccardodellacorna.readinglist.core.model.SavedBook
import com.riccardodellacorna.readinglist.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun saved(
        id: String,
        status: ReadingStatus,
    ) = SavedBook(
        book = Book(
            id = id,
            title = "Title $id",
            authors = emptyList(),
            firstPublishYear = null,
            coverUrl = null
        ),
        status = status,
        addedAt = 0L
    )

    private val repository = FakeLibraryRepository(
        initial = listOf(
            saved(
                "A",
                ReadingStatus.TO_READ
            ),
            saved(
                "B",
                ReadingStatus.READING
            ),
            saved(
                "C",
                ReadingStatus.READ
            )
        )
    )

    /**
     * L’ordine costruzione istanza → rule → @Before è una domanda classica. Se un oggetto usa
     * Dispatchers.Main nel costruttore o in un inizializzatore, non può essere un campo con valore immediato:
     * va creato in @Before, oppure con by lazy. Ti ho fatto vedere un bug che si presenta davvero nei progetti reali,
     * non solo nei tutorial.
     *
     * Prima era:
     * private val viewModel = LibraryViewModel(repository)
     * **/
    private lateinit var viewModel: LibraryViewModel

    @Before
    fun setUp() {
        viewModel = LibraryViewModel(repository)
    }

    private fun TestScope.keepStateActive() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    @Test
    fun `initial state is loading before anyone observes`() {
        assertThat(viewModel.uiState.value.isLoading).isTrue()
    }

    @Test
    fun `shows all books when no filter is set`() = runTest {
        keepStateActive()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.books.map { it.book.id }).containsExactly(
            "A",
            "B",
            "C"
        )
    }

    @Test
    fun `filter keeps only the books with that status`() = runTest {
        keepStateActive()

        viewModel.onFilterChange(ReadingStatus.READING)

        val state = viewModel.uiState.value
        assertThat(state.filter).isEqualTo(ReadingStatus.READING)
        assertThat(state.books.map { it.book.id }).containsExactly("B")
    }

    @Test
    fun `removing the filter shows everything again`() = runTest {
        keepStateActive()
        viewModel.onFilterChange(ReadingStatus.READ)

        viewModel.onFilterChange(null)

        assertThat(viewModel.uiState.value.books).hasSize(3)
    }

    @Test
    fun `changing a status moves the book out of the current filter`() = runTest {
        keepStateActive()
        viewModel.onFilterChange(ReadingStatus.TO_READ)

        viewModel.onStatusChange(
            "A",
            ReadingStatus.READ
        )

        assertThat(viewModel.uiState.value.books).isEmpty()
    }

    @Test
    fun `removing a book updates the list`() = runTest {
        keepStateActive()

        viewModel.onRemove("B")

        assertThat(viewModel.uiState.value.books.map { it.book.id }).containsExactly(
            "A",
            "C"
        )
    }
}
