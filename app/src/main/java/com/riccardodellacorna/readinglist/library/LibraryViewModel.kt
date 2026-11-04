package com.riccardodellacorna.readinglist.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riccardodellacorna.readinglist.core.model.ReadingStatus
import com.riccardodellacorna.readinglist.library.domain.ILibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val libraryRepository: ILibraryRepository,
) : ViewModel() {

    private val filter = MutableStateFlow<ReadingStatus?>(null)

    val uiState: StateFlow<LibraryUiState> = combine(
        libraryRepository.observeLibrary(),
        filter
    ) { books, filter ->
        LibraryUiState(
            books = if (filter == null) books else books.filter { it.status == filter },
            filter = filter,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LibraryUiState()
    )

    fun onFilterChange(status: ReadingStatus?) {
        filter.value = status
    }

    fun onStatusChange(
        id: String,
        status: ReadingStatus,
    ) {
        viewModelScope.launch {
            libraryRepository.updateStatus(
                id,
                status
            )
        }
    }

    fun onRemove(id: String) {
        viewModelScope.launch { libraryRepository.remove(id) }
    }
}
