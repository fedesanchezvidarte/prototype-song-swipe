package org.ilerna.song_swipe_frontend.presentation.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.ThemeMode
import org.ilerna.song_swipe_frontend.presentation.components.SettingItemWithButton
import org.ilerna.song_swipe_frontend.presentation.components.SettingItemWithDropdown
import org.ilerna.song_swipe_frontend.presentation.theme.ContentAlphaMedium
import org.ilerna.song_swipe_frontend.presentation.theme.SongSwipeTheme
import org.ilerna.song_swipe_frontend.presentation.theme.Spacing

/**
 * Settings Screen - User preferences and app configuration.
 * Includes theme mode selection with DataStore persistence.
 * 
 * @param viewModel ViewModel for managing settings state
 * @param modifier Optional modifier
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val isSigningOut by viewModel.isSigningOut.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(Spacing.spaceLg)
    ) {
        // Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = colorScheme.secondary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = Spacing.spaceMd)
        )

        // Theme Setting
        SettingItemWithDropdown(
            title = "Theme",
            description = "Select the theme for the app",
            selectedOption = currentTheme,
            options = ThemeMode.entries.toList(),
            optionLabel = { it.getDisplayName() },
            onOptionSelected = { theme -> viewModel.setTheme(theme) },
            contentDescription = "Select theme"
        )
        
        // Sign Out Setting
        SettingItemWithButton(
            title = "Account",
            description = "Sign out of your account",
            buttonText = if (isSigningOut) "Signing out..." else "Sign Out",
            onButtonClick = { viewModel.signOut() },
            modifier = Modifier.padding(top = Spacing.spaceMd)
        )
    }
}

/* PREVIEWS */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSettingsScreen() {
    SongSwipeTheme {
        // Preview without ViewModel - just show structure
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(Spacing.spaceLg)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = Spacing.spaceMd)
            )
            
            SettingItemWithDropdown(
                title = "Theme",
                description = "Select the theme for the app",
                selectedOption = ThemeMode.SYSTEM,
                options = ThemeMode.entries.toList(),
                optionLabel = { it.getDisplayName() },
                onOptionSelected = {},
                contentDescription = "Select theme"
            )
            
            SettingItemWithButton(
                title = "Account",
                description = "Sign out of your account",
                buttonText = "Sign Out",
                onButtonClick = {},
                modifier = Modifier.padding(top = Spacing.spaceMd)
            )
        }
    }
}

