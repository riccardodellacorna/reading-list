package com.riccardodellacorna.readinglist.search

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.riccardodellacorna.readinglist.core.common.AppResult
import com.riccardodellacorna.readinglist.core.common.DataError
import com.riccardodellacorna.readinglist.core.model.Book
import com.riccardodellacorna.readinglist.library.FakeLibraryRepository
import com.riccardodellacorna.readinglist.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val searchRepository = FakeSearchRepository()
    private val libraryRepository = FakeLibraryRepository()

    /**
     * L’ordine costruzione istanza → rule → @Before è una domanda classica. Se un oggetto usa
     * Dispatchers.Main nel costruttore o in un inizializzatore, non può essere un campo con valore immediato:
     * va creato in @Before, oppure con by lazy. Ti ho fatto vedere un bug che si presenta davvero nei progetti reali,
     * non solo nei tutorial.
     *
     * Prima era:
     * private val viewModel = SearchViewModel(
     *         searchRepository,
     *         libraryRepository
     *     )
     * **/
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        viewModel = SearchViewModel(
            searchRepository,
            libraryRepository
        )
    }

    private val dune = Book(
        "OL1W",
        "Dune",
        listOf("Frank Herbert"),
        1965,
        null
    )

    /** Mantiene attivo lo stateIn(WhileSubscribed): senza un collector lo stato non si aggiorna. */
    private fun TestScope.keepStateActive() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    @Test
    fun `initial state is idle with an empty query`() = runTest {
        keepStateActive()

        assertThat(viewModel.uiState.value).isEqualTo(SearchUiState())
    }

    @Test
    fun `search starts only after the debounce time`() = runTest {
        keepStateActive()

        viewModel.onQueryChange("dune")
        advanceTimeBy(299.milliseconds)
        assertThat(searchRepository.queries).isEmpty()

        advanceTimeBy(2.milliseconds)
        assertThat(searchRepository.queries).containsExactly("dune")
    }

    @Test
    fun `fast typing triggers a single search with the last query`() = runTest {
        keepStateActive()

        viewModel.onQueryChange("d")
        advanceTimeBy(100.milliseconds)
        viewModel.onQueryChange("du")
        advanceTimeBy(100.milliseconds)
        viewModel.onQueryChange("dun")
        advanceUntilIdle()

        assertThat(searchRepository.queries).containsExactly("dun")
    }

    @Test
    fun `successful search shows the results`() = runTest {
        keepStateActive()
        searchRepository.result = AppResult.Success(listOf(dune))

        viewModel.onQueryChange("dune")
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.content).isEqualTo(
            SearchContent.Results(
                listOf(
                    SearchItem(
                        dune,
                        isSaved = false
                    )
                )
            )
        )
    }

    @Test
    fun `search without results shows the empty state`() = runTest {
        keepStateActive()
        searchRepository.result = AppResult.Success(emptyList())

        viewModel.onQueryChange("zzzz")
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.content).isEqualTo(SearchContent.Empty)
    }

    @Test
    fun `network failure shows the error state`() = runTest {
        keepStateActive()
        searchRepository.result = AppResult.Failure(DataError.Network)

        viewModel.onQueryChange("dune")
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.content).isEqualTo(SearchContent.Error(DataError.Network))
    }

    @Test
    fun `clearing the query goes back to idle without calling the repository again`() = runTest {
        keepStateActive()
        searchRepository.result = AppResult.Success(listOf(dune))
        viewModel.onQueryChange("dune")
        advanceUntilIdle()

        viewModel.onQueryChange("")
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.content).isEqualTo(SearchContent.Idle)
        assertThat(searchRepository.queries).containsExactly("dune")
    }

    @Test
    fun `books already in the library are marked as saved`() = runTest {
        keepStateActive()
        searchRepository.result = AppResult.Success(listOf(dune))
        viewModel.onQueryChange("dune")
        advanceUntilIdle()

        viewModel.onSaveClick(dune)
        advanceUntilIdle()

        val content = viewModel.uiState.value.content as SearchContent.Results
        assertThat(content.items.single().isSaved).isTrue()
    }

    @Test
    fun `loading state is emitted before the results`() = runTest {
        searchRepository.result = AppResult.Success(listOf(dune))
        searchRepository.delayMillis = 100

        viewModel.uiState.test {
            assertThat(awaitItem().content).isEqualTo(SearchContent.Idle)

            viewModel.onQueryChange("dune")
            assertThat(awaitItem().query).isEqualTo("dune")
            assertThat(awaitItem().content).isEqualTo(SearchContent.Loading)
            assertThat(awaitItem().content).isInstanceOf(SearchContent.Results::class.java)
        }
    }
}
