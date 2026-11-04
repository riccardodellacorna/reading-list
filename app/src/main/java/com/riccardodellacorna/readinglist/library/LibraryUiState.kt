package com.riccardodellacorna.readinglist.library

import com.riccardodellacorna.readinglist.core.model.ReadingStatus
import com.riccardodellacorna.readinglist.core.model.SavedBook

data class LibraryUiState(
    val books: List<SavedBook> = emptyList(),
    val filter: ReadingStatus? = null,
    val isLoading: Boolean = true
)
