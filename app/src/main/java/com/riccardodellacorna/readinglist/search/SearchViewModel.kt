package com.riccardodellacorna.readinglist.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riccardodellacorna.readinglist.core.common.AppResult
import com.riccardodellacorna.readinglist.core.model.Book
import com.riccardodellacorna.readinglist.library.domain.ILibraryRepository
import com.riccardodellacorna.readinglist.search.domain.ISearchRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * collectLatest: se arriva una nuova query mentre la precedente è in corso, la vecchia ricerca viene cancellata. Evita risposte fuori ordine.
 * debounce + distinctUntilChanged: niente chiamata a ogni tasto, né ripetuta per la stessa query.
 * combine con la libreria: lo stato “salvato” dei risultati si aggiorna da solo.
 * WhileSubscribed(5_000): il flusso si ferma 5 secondi dopo che la UI smette di osservarlo, così sopravvive alle rotazioni senza lavorare in background.
 * Limite noto: dopo un errore, ridigitare la stessa query non riprova (distinctUntilChanged). Il retry lo aggiungiamo più avanti con la UI.
 */

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val searchRepository: ISearchRepository,
    private val libraryRepository: ILibraryRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val content = MutableStateFlow<SearchContent>(SearchContent.Idle)

    val uiState: StateFlow<SearchUiState> = combine(
        query,
        content,
        libraryRepository.observeLibrary()
    ) { query, content, saved ->
        val savedIds = saved.map { it.book.id }.toSet()
        val visibleContent = if (content is SearchContent.Results) {
            SearchContent.Results(content.items.map { it.copy(isSaved = it.book.id in savedIds) })
        } else {
            content
        }
        SearchUiState(
            query = query,
            content = visibleContent
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchUiState()
    )

    init {
        viewModelScope.launch {
            query.debounce(
                DEBOUNCE_MILLIS.milliseconds
            ).map {
                it.trim()
            }.distinctUntilChanged().collectLatest { text ->
                runSearch(text)
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }

    fun onSaveClick(book: Book) {
        viewModelScope.launch {
            libraryRepository.save(book)
        }
    }

    private suspend fun runSearch(text: String) {
        if (text.isEmpty()) {
            content.value = SearchContent.Idle
            return
        }
        content.value = SearchContent.Loading
        content.value = when (val result = searchRepository.search(text)) {
            is AppResult.Success -> {
                if (result.data.isEmpty()) {
                    SearchContent.Empty
                } else {
                    SearchContent.Results(
                        result.data.map {
                            SearchItem(
                                it,
                                isSaved = false
                            )
                        })
                }
            }
            is AppResult.Failure -> {
                SearchContent.Error(result.error)
            }
        }
    }

    private companion object {
        const val DEBOUNCE_MILLIS = 300L
    }
}
