package org.ilerna.song_swipe_frontend.presentation.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.ilerna.song_swipe_frontend.presentation.theme.SongSwipeTheme
import org.ilerna.song_swipe_frontend.presentation.theme.Spacing

/**
 * Settings Screen - Placeholder for future settings functionality.
 * Will include user preferences, account settings, and app configuration.
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Spacing.spaceLg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Placeholder Icon
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(Spacing.spaceMd))

            // Title
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(Spacing.spaceSm))

            // Description
            Text(
                text = "Customize your experience, manage your account, and configure app preferences.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.spaceXl))

            // Coming soon badge
            Text(
                text = "🚧 Coming Soon",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

/* PREVIEWS */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSettingsScreen() {
    SongSwipeTheme {
        SettingsScreen()
    }
}
