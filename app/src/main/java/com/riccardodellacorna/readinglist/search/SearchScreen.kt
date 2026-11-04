package com.riccardodellacorna.readinglist.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.riccardodellacorna.readinglist.core.common.DataError
import com.riccardodellacorna.readinglist.core.model.Book
import com.riccardodellacorna.readinglist.core.ui.BookItem
import com.riccardodellacorna.readinglist.core.ui.CenteredMessage
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchRoute(viewModel: SearchViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SearchScreen(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onSaveClick = viewModel::onSaveClick,
    )
}

@Composable
fun SearchScreen(
    state: SearchUiState,
    onQueryChange: (String) -> Unit,
    onSaveClick: (Book) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("search_field"),
            value = state.query,
            onValueChange = onQueryChange,
            label = {
                Text(
                    "Search books",
                )
            },
            singleLine = true,
            trailingIcon = {
                if (state.query.isNotEmpty()) {
                    IconButton(
                        onClick = { onQueryChange("") },
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear search",
                        )
                    }
                }
            },
        )

        when (val content = state.content) {
            SearchContent.Idle -> {
                CenteredMessage(
                    text = "Type a title or an author",
                )
            }
            SearchContent.Loading -> {
                Box(
                    Modifier.fillMaxSize(),
                    Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        Modifier.testTag("loading"),
                    )
                }
            }
            SearchContent.Empty -> {
                CenteredMessage(
                    text = "No books found",
                )
            }
            is SearchContent.Error -> {
                CenteredMessage(
                    text = content.error.message(),
                )
            }
            is SearchContent.Results -> {
                LazyColumn {
                    items(
                        content.items,
                        key = {
                            it.book.id
                        },
                    ) { item ->
                        BookItem(book = item.book) {
                            if (item.isSaved) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Saved",
                                )
                            } else {
                                IconButton(
                                    onClick = {
                                        onSaveClick(item.book)
                                    },
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Save ${item.book.title}",
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun DataError.message(): String = when (this) {
    DataError.Network -> {
        "No connection. Check your network and try again."
    }
    is DataError.Server -> {
        "The server is unavailable (error $code)."
    }
    DataError.Unknown -> {
        "Something went wrong."
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    val dune = Book(
        "OL1W",
        "Dune",
        listOf("Frank Herbert"),
        1965,
        null,
    )
    SearchScreen(
        state = SearchUiState(
            query = "dune",
            content = SearchContent.Results(
                listOf(
                    SearchItem(
                        dune,
                        isSaved = false,
                    ),
                ),
            ),
        ),
        onQueryChange = {},
        onSaveClick = {},
    )
}
