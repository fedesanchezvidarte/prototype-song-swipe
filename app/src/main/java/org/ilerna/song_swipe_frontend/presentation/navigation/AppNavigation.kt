package org.ilerna.song_swipe_frontend.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.ilerna.song_swipe_frontend.domain.model.User
import org.ilerna.song_swipe_frontend.presentation.screen.home.HomeScreen
import org.ilerna.song_swipe_frontend.presentation.screen.home.HomeViewModel
import org.ilerna.song_swipe_frontend.presentation.screen.playlists.PlaylistsScreen
import org.ilerna.song_swipe_frontend.presentation.screen.settings.SettingsScreen
import org.ilerna.song_swipe_frontend.presentation.screen.settings.SettingsViewModel
import org.ilerna.song_swipe_frontend.presentation.screen.swipe.SwipeScreen
import org.ilerna.song_swipe_frontend.presentation.screen.swipe.SwipeViewModel

/**
 * Main navigation host for the app.
 * Handles navigation between all screens after authentication.
 *
 * @param navController The NavController to manage navigation
 * @param user The current logged-in user
 * @param settingsViewModel Shared SettingsViewModel for theme management
 * @param modifier Modifier for the NavHost
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    user: User?,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // Home Screen
        composable(route = Screen.Home.route) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = homeViewModel,
                user = user,
                onCategoryClick = { category ->
                    // TODO: Navigate to category detail screen
                },
                onSwipeClick = {
                    navController.navigate(Screen.Swipe.route)
                }
            )
        }

        // Playlists Screen
        composable(route = Screen.Playlists.route) {
            PlaylistsScreen()
        }

        // Settings Screen
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel
            )
        }

        // Swipe Screen (detail screen, not in bottom nav)
        composable(route = Screen.Swipe.route) {
            val swipeViewModel: SwipeViewModel = hiltViewModel()
            SwipeScreen(
                viewModel = swipeViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
