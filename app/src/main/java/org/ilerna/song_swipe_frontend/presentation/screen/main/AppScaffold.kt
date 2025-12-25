package org.ilerna.song_swipe_frontend.presentation.screen.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.ilerna.song_swipe_frontend.domain.model.User
import org.ilerna.song_swipe_frontend.presentation.navigation.BottomNavItem
import org.ilerna.song_swipe_frontend.presentation.navigation.BottomNavigationBar
import org.ilerna.song_swipe_frontend.presentation.screen.home.HomeScreen
import org.ilerna.song_swipe_frontend.presentation.screen.playlists.PlaylistsScreen
import org.ilerna.song_swipe_frontend.presentation.screen.settings.SettingsScreen
import org.ilerna.song_swipe_frontend.presentation.screen.settings.SettingsViewModel
import org.ilerna.song_swipe_frontend.presentation.screen.swipe.SwipeScreen
import org.ilerna.song_swipe_frontend.presentation.screen.swipe.SwipeViewModel

/**
 * App Scaffold with bottom navigation bar.
 * This is the main container that hosts Home, Playlists, Settings ... screens.
 *
 * @param user The current logged-in user
 * @param settingsViewModel ViewModel for managing settings state
 * @param modifier Modifier for the screen
 */
@Composable
fun AppScaffold(
    user: User?,
    settingsViewModel: SettingsViewModel,
    swipeViewModel: SwipeViewModel,
    modifier: Modifier = Modifier
) {
    var currentRoute by rememberSaveable { mutableStateOf(BottomNavItem.Home.route) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onItemClick = { item ->
                    currentRoute = item.route
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            // Content based on current route
            when (currentRoute) {
                BottomNavItem.Home.route -> {
                    HomeScreen(
                        user = user,
                        onCategoryClick = { category ->
                            // TODO: Navigate to category detail screen
                        },
                        onSwipeClick = {
                            currentRoute = "swipe"
                        }
                    )
                }
                BottomNavItem.Playlists.route -> {
                    PlaylistsScreen()
                }
                BottomNavItem.Settings.route -> {
                    SettingsScreen(
                        viewModel = settingsViewModel
                    )
                }
                "swipe" -> {
                    SwipeScreen(
                        viewModel = swipeViewModel,
                        onNavigateBack = {
                            currentRoute = BottomNavItem.Home.route
                        }
                    )
                }
            }
        }
    }
}
