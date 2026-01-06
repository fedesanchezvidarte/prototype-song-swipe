package org.ilerna.song_swipe_frontend.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import org.ilerna.song_swipe_frontend.core.network.NetworkResult
import org.ilerna.song_swipe_frontend.domain.model.User
import org.ilerna.song_swipe_frontend.domain.usecase.category.GetCategoryPlaylistsUseCase
import org.ilerna.song_swipe_frontend.domain.usecase.category.GetFeaturedPlaylistsUseCase
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
 * @param getCategoryPlaylistsUseCase Use case for fetching category playlists
 * @param getFeaturedPlaylistsUseCase Use case for fetching featured playlists
 * @param modifier Modifier for the NavHost
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    user: User?,
    settingsViewModel: SettingsViewModel,
    getCategoryPlaylistsUseCase: GetCategoryPlaylistsUseCase,
    getFeaturedPlaylistsUseCase: GetFeaturedPlaylistsUseCase,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

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
                    // Fetch a random playlist from the category and navigate to SwipeScreen
                    coroutineScope.launch {
                        when (val result = getCategoryPlaylistsUseCase.getRandomPlaylist(category.id)) {
                            is NetworkResult.Success -> {
                                navController.navigate(Screen.Swipe.createRoute(result.data.id))
                            }
                            is NetworkResult.Error -> {
                                // TODO: Show error to user (snackbar or toast)
                                // For now, navigate to swipe with default playlist
                                navController.navigate(Screen.Swipe.createRoute())
                            }
                            is NetworkResult.Loading -> {
                                // Loading state handled by UI
                            }
                        }
                    }
                },
                onSwipeClick = {
                    // Fetch a random featured playlist and navigate to SwipeScreen
                    coroutineScope.launch {
                        when (val result = getFeaturedPlaylistsUseCase.getRandomPlaylist()) {
                            is NetworkResult.Success -> {
                                navController.navigate(Screen.Swipe.createRoute(result.data.id))
                            }
                            is NetworkResult.Error -> {
                                // Fallback to default playlist on error
                                navController.navigate(Screen.Swipe.createRoute())
                            }
                            is NetworkResult.Loading -> {
                                // Loading state handled by UI
                            }
                        }
                    }
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
        composable(
            route = Screen.Swipe.ROUTE_PATTERN,
            arguments = listOf(
                navArgument(Screen.Swipe.ARG_PLAYLIST_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getString(Screen.Swipe.ARG_PLAYLIST_ID)
            val swipeViewModel: SwipeViewModel = hiltViewModel()
            SwipeScreen(
                viewModel = swipeViewModel,
                playlistId = playlistId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
