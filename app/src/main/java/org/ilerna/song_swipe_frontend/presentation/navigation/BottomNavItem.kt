package org.ilerna.song_swipe_frontend.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class representing bottom navigation items.
 * Each item has a screen, title, and icons for selected/unselected states.
 * Uses Screen routes for type-safe navigation.
 */
sealed class BottomNavItem(
    val screen: Screen,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    /** Route string from the associated Screen */
    val route: String get() = screen.route
    
    data object Home : BottomNavItem(
        screen = Screen.Home,
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    data object Playlists : BottomNavItem(
        screen = Screen.Playlists,
        title = "Playlists",
        selectedIcon = Icons.AutoMirrored.Filled.PlaylistPlay,
        unselectedIcon = Icons.AutoMirrored.Outlined.PlaylistPlay
    )

    data object Settings : BottomNavItem(
        screen = Screen.Settings,
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
