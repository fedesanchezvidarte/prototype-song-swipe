package org.ilerna.song_swipe_frontend.presentation.screen.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.ilerna.song_swipe_frontend.domain.model.User
import org.ilerna.song_swipe_frontend.presentation.navigation.BottomNavItem
import org.ilerna.song_swipe_frontend.presentation.navigation.BottomNavigationBar
import org.ilerna.song_swipe_frontend.presentation.screen.home.HomeScreen
import org.ilerna.song_swipe_frontend.presentation.screen.playlists.PlaylistsScreen
import org.ilerna.song_swipe_frontend.presentation.screen.settings.SettingsScreen
import org.ilerna.song_swipe_frontend.presentation.screen.settings.SettingsViewModel
import org.ilerna.song_swipe_frontend.presentation.theme.SongSwipeTheme

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
                    // Placeholder for SwipeScreen (to be implemented in next step)
                    PlaceholderSwipeScreen(
                        onBackClick = {
                            currentRoute = BottomNavItem.Home.route
                        }
                    )
                }
            }
        }
    }
}

/**
 * Placeholder screen for Swipe feature.
 * Will be replaced with actual SwipeScreen implementation in next steps.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceholderSwipeScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Swipe") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        androidx.compose.foundation.layout.Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Swipe Screen\n(Coming Soon)",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
