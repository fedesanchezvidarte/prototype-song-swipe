package org.ilerna.song_swipe_frontend.presentation.screen.playlists

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
import androidx.compose.material.icons.automirrored.outlined.PlaylistPlay
import androidx.compose.material.icons.outlined.PlaylistPlay
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
 * Playlists Screen - Placeholder for future playlist functionality.
 * Will display user's playlists and liked songs.
 */
@Composable
fun PlaylistsScreen(
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
                imageVector = Icons.AutoMirrored.Outlined.PlaylistPlay,
                contentDescription = "Playlists",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(Spacing.spaceMd))

            // Title
            Text(
                text = "Your Playlists",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(Spacing.spaceSm))

            // Description
            Text(
                text = "Your liked songs and created playlists will appear here.",
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
fun PreviewPlaylistsScreen() {
    SongSwipeTheme {
        PlaylistsScreen()
    }
}
