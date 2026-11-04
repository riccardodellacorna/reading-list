package com.riccardodellacorna.readinglist.library

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.riccardodellacorna.readinglist.core.model.ReadingStatus
import com.riccardodellacorna.readinglist.core.model.SavedBook
import com.riccardodellacorna.readinglist.core.ui.BookItem
import com.riccardodellacorna.readinglist.core.ui.CenteredMessage
import com.riccardodellacorna.readinglist.core.ui.label
import org.koin.androidx.compose.koinViewModel

@Composable
fun LibraryRoute(viewModel: LibraryViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LibraryScreen(
        state = state,
        onFilterChange = viewModel::onFilterChange,
        onStatusChange = viewModel::onStatusChange,
        onRemove = viewModel::onRemove,
    )
}

@Composable
fun LibraryScreen(
    state: LibraryUiState,
    onFilterChange: (ReadingStatus?) -> Unit,
    onStatusChange: (String, ReadingStatus) -> Unit,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp,
                ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                modifier = Modifier.testTag("filter_ALL"),
                selected = state.filter == null,
                onClick = { onFilterChange(null) },
                label = {
                    Text("All")
                },
            )

            ReadingStatus.entries.forEach { status ->
                FilterChip(
                    modifier = Modifier.testTag("filter_${status.name}"),
                    selected = state.filter == status,
                    onClick = { onFilterChange(status) },
                    label = {
                        Text(status.label())
                    },
                )
            }
        }

        when {
            state.isLoading -> {
                Box(
                    Modifier.fillMaxSize(),
                    Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            state.books.isEmpty() -> {
                CenteredMessage(
                    text = if (state.filter == null) {
                        "Your library is empty. Search for a book to add it."
                    } else {
                        "No books with this status"
                    },
                )
            }
            else -> LazyColumn {
                items(
                    items = state.books,
                    key = { it.book.id },
                ) { saved ->
                    BookItem(
                        book = saved.book,
                    ) {
                        StatusMenu(saved) {
                            onStatusChange(
                                saved.book.id,
                                it,
                            )
                        }

                        IconButton(
                            onClick = {
                                onRemove(saved.book.id)
                            },
                            content = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Remove ${saved.book.title}",
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusMenu(
    saved: SavedBook,
    onStatusChange: (ReadingStatus) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(
            modifier = Modifier.testTag("status_${saved.book.id}"),
            onClick = {
                expanded = true
            },
        ) {
            Text(saved.status.label())
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            ReadingStatus.entries.forEach { status ->
                DropdownMenuItem(
                    modifier = Modifier.testTag("status_option_${status.name}"),
                    text = { Text(status.label()) },
                    onClick = {
                        expanded = false
                        onStatusChange(status)
                    },
                )
            }
        }
    }
}
