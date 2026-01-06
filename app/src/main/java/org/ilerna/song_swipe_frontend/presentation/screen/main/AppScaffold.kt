package org.ilerna.song_swipe_frontend.presentation.screen.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.ilerna.song_swipe_frontend.domain.model.User
import org.ilerna.song_swipe_frontend.domain.usecase.category.GetCategoryPlaylistsUseCase
import org.ilerna.song_swipe_frontend.domain.usecase.category.GetFeaturedPlaylistsUseCase
import org.ilerna.song_swipe_frontend.presentation.navigation.AppNavigation
import org.ilerna.song_swipe_frontend.presentation.navigation.BottomNavigationBar
import org.ilerna.song_swipe_frontend.presentation.navigation.Screen
import org.ilerna.song_swipe_frontend.presentation.screen.settings.SettingsViewModel

/**
 * App Scaffold with bottom navigation bar and NavHost.
 * This is the main container that hosts Home, Playlists, Settings, and detail screens.
 *
 * @param user The current logged-in user
 * @param settingsViewModel ViewModel for managing settings state
 * @param getCategoryPlaylistsUseCase Use case for fetching category playlists
 * @param getFeaturedPlaylistsUseCase Use case for fetching featured playlists
 * @param navController NavController for managing navigation (optional, creates one if not provided)
 * @param modifier Modifier for the screen
 */
@Composable
fun AppScaffold(
    user: User?,
    settingsViewModel: SettingsViewModel,
    getCategoryPlaylistsUseCase: GetCategoryPlaylistsUseCase,
    getFeaturedPlaylistsUseCase: GetFeaturedPlaylistsUseCase,
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    // Get current route to determine if bottom bar should be shown
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Only show bottom bar for main screens (not detail screens like Swipe)
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Playlists.route,
        Screen.Settings.route
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            user = user,
            settingsViewModel = settingsViewModel,
            getCategoryPlaylistsUseCase = getCategoryPlaylistsUseCase,
            getFeaturedPlaylistsUseCase = getFeaturedPlaylistsUseCase,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
