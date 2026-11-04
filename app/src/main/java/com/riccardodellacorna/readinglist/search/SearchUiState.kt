package com.riccardodellacorna.readinglist.search

import com.riccardodellacorna.readinglist.core.common.DataError
import com.riccardodellacorna.readinglist.core.model.Book

data class SearchUiState(
    val query: String = "",
    val content: SearchContent = SearchContent.Idle,
)

data class SearchItem(
    val book: Book,
    val isSaved: Boolean,
)

sealed interface SearchContent {
    data object Idle : SearchContent
    data object Loading : SearchContent
    data object Empty : SearchContent
    data class Results(val items: List<SearchItem>) : SearchContent
    data class Error(val error: DataError) : SearchContent
}
