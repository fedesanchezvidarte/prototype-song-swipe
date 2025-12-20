package org.ilerna.song_swipe_frontend.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlaylistPlay
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class representing bottom navigation items.
 * Each item has a route, title, and icons for selected/unselected states.
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : BottomNavItem(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    data object Playlists : BottomNavItem(
        route = "playlists",
        title = "Playlists",
        selectedIcon = Icons.Filled.PlaylistPlay,
        unselectedIcon = Icons.Outlined.PlaylistPlay
    )

    data object Settings : BottomNavItem(
        route = "settings",
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
}

/**
 * List of all bottom navigation items for easy iteration.
 */
val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Playlists,
    BottomNavItem.Settings
)
