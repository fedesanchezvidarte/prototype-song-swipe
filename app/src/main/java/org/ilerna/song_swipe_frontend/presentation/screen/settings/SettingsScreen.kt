package org.ilerna.song_swipe_frontend.presentation.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.ilerna.song_swipe_frontend.data.datasource.local.preferences.ThemeMode
import org.ilerna.song_swipe_frontend.presentation.theme.SongSwipeTheme
import org.ilerna.song_swipe_frontend.presentation.theme.Spacing

/**
 * Settings Screen - User preferences and app configuration.
 * Includes theme mode selection with DataStore persistence.
 * 
 * @param viewModel ViewModel for managing settings state
 * @param onThemeChanged Callback when theme is changed
 * @param modifier Optional modifier
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onThemeChanged: (ThemeMode) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Spacing.spaceLg)
    ) {
        // Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = colors.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = Spacing.spaceMd)
        )

        // Theme Setting
        SettingItemWithDropdown(
            title = "Theme",
            description = "Select the theme for the app",
            selectedOption = currentTheme,
            options = ThemeMode.entries,
            onOptionSelected = { theme ->
                viewModel.setTheme(theme)
                onThemeChanged(theme)
            },
            contentDescription = "Select theme"
        )
    }
}

/**
 * SettingItemWithDropdown - Reusable component for a setting with dropdown selector
 * 
 * @param title The title of the setting
 * @param description The description of the setting
 * @param selectedOption The currently selected option
 * @param options List of all available options
 * @param onOptionSelected Callback when an option is selected
 * @param contentDescription Content description for accessibility
 * @param modifier Optional modifier
 */
@Composable
fun SettingItemWithDropdown(
    title: String,
    description: String,
    selectedOption: ThemeMode,
    options: List<ThemeMode>,
    onOptionSelected: (ThemeMode) -> Unit,
    contentDescription: String = "Select option",
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurface.copy(alpha = 0.7f)
            )
        }

        // Dropdown Button
        TextButton(
            onClick = { expanded = true }
        ) {
            Text(
                text = selectedOption.getDisplayName(),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.primary
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = contentDescription,
                tint = colors.primary
            )
        }

        // Dropdown Menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.getDisplayName(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
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
                options = ThemeMode.entries,
                onOptionSelected = {},
                contentDescription = "Select theme"
            )
        }
    }
}

