package com.riccardodellacorna.readinglist.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.riccardodellacorna.readinglist.library.LibraryRoute
import com.riccardodellacorna.readinglist.search.SearchRoute

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                TopLevelDestination.entries.forEach { item ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any {
                            it.hasRoute(item.route::class)
                        } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = null,
                            )
                        },
                        label = {
                            Text(item.label)
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = SearchDestination,
        ) {
            composable<SearchDestination> {
                SearchRoute()
            }
            composable<LibraryDestination> {
                LibraryRoute()
            }
        }
    }
}
