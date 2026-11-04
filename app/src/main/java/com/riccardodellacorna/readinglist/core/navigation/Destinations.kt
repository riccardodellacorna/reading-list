package com.riccardodellacorna.readinglist.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
data object SearchDestination
@Serializable
data object LibraryDestination

enum class TopLevelDestination(
    val route: Any,
    val label: String,
    val icon: ImageVector,
) {
    SEARCH(
        SearchDestination,
        "Search",
        Icons.Default.Search,
    ),
    LIBRARY(
        LibraryDestination,
        "Library",
        Icons.AutoMirrored.Filled.List,
    )
}
