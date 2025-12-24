package org.ilerna.song_swipe_frontend.presentation.screen.swipe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.ilerna.song_swipe_frontend.domain.model.Album
import org.ilerna.song_swipe_frontend.domain.model.Artist
import org.ilerna.song_swipe_frontend.domain.model.Track
import org.ilerna.song_swipe_frontend.presentation.theme.NeonCyan
import org.ilerna.song_swipe_frontend.presentation.theme.NeonGreen
import org.ilerna.song_swipe_frontend.presentation.theme.NeonPink
import org.ilerna.song_swipe_frontend.presentation.theme.NeonRed
import org.ilerna.song_swipe_frontend.presentation.theme.SongSwipeTheme
import org.ilerna.song_swipe_frontend.presentation.theme.Spacing

/**
 * Swipe screen for discovering new music by swiping through tracks
 *
 * @param viewModel The SwipeViewModel managing the screen state
 * @param onNavigateBack Callback to navigate back to the previous screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeScreen(
    viewModel: SwipeViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    // Load tracks when the screen is first displayed
    LaunchedEffect(Unit) {
        if (state is SwipeState.Idle) {
            viewModel.loadTracks()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Discover",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            when (val currentState = state) {
                is SwipeState.Idle,
                is SwipeState.Loading -> {
                    LoadingContent()
                }

                is SwipeState.Success -> {
                    if (currentState.hasMoreTracks && currentState.currentTrack != null) {
                        SwipeContent(
                            track = currentState.currentTrack!!,
                            progress = currentState.progress,
                            tracksRemaining = currentState.tracks.size - currentState.currentIndex,
                            onLike = viewModel::onLike,
                            onDislike = viewModel::onDislike
                        )
                    } else {
                        CompletedContent(
                            likedCount = currentState.likedTracks.size,
                            totalCount = currentState.tracks.size,
                            onRestart = viewModel::retry
                        )
                    }
                }

                is SwipeState.Error -> {
                    ErrorContent(
                        message = currentState.message,
                        onRetry = viewModel::retry
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = NeonCyan,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(Spacing.spaceMd))
        Text(
            text = "Loading tracks...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun SwipeContent(
    track: Track,
    progress: Float,
    tracksRemaining: Int,
    onLike: () -> Unit,
    onDislike: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.spaceMd),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress indicator
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = NeonCyan,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(Spacing.spaceSm))

        Text(
            text = "$tracksRemaining tracks remaining",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(Spacing.spaceLg))

        // Track Card
        TrackCard(track = track)

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons
        ActionButtons(
            onLike = onLike,
            onDislike = onDislike
        )

        Spacer(modifier = Modifier.height(Spacing.spaceXl))
    }
}

@Composable
private fun TrackCard(
    track: Track,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.spaceMd),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.spaceMd),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Album Art
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                if (track.album.imageUrl != null) {
                    AsyncImage(
                        model = track.album.imageUrl,
                        contentDescription = "Album art for ${track.album.name}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.spaceMd))

            // Track Name
            Text(
                text = track.name,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Spacing.spaceSm))

            // Artist Names
            Text(
                text = track.artists.joinToString(", ") { it.name },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Spacing.spaceXs))

            // Album Name
            Text(
                text = track.album.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Preview URL indicator
            if (track.previewUrl != null) {
                Spacer(modifier = Modifier.height(Spacing.spaceSm))
                Text(
                    text = "🎵 Preview available",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonGreen.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    onLike: () -> Unit,
    onDislike: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dislike Button
        IconButton(
            onClick = onDislike,
            modifier = Modifier
                .size(72.dp)
                .background(
                    color = NeonRed.copy(alpha = 0.15f),
                    shape = CircleShape
                ),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = NeonRed
            )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dislike",
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.width(Spacing.spaceXl))

        // Like Button
        IconButton(
            onClick = onLike,
            modifier = Modifier
                .size(72.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(NeonPink, NeonCyan)
                    ),
                    shape = CircleShape
                ),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Like",
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
private fun CompletedContent(
    likedCount: Int,
    totalCount: Int,
    onRestart: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(Spacing.spaceLg)
    ) {
        Text(
            text = "🎉",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(Spacing.spaceMd))

        Text(
            text = "All done!",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(Spacing.spaceSm))

        Text(
            text = "You liked $likedCount out of $totalCount tracks",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.spaceXl))

        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonCyan
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.spaceSm))
            Text("Discover More")
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(Spacing.spaceLg)
    ) {
        Text(
            text = "😕",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(Spacing.spaceMd))

        Text(
            text = "Oops! Something went wrong",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(Spacing.spaceSm))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.spaceXl))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonCyan
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.spaceSm))
            Text("Try Again")
        }
    }
}

// ==================== Previews ====================

@Preview(showBackground = true)
@Composable
private fun TrackCardPreview() {
    SongSwipeTheme {
        TrackCard(
            track = Track(
                id = "1",
                name = "Bohemian Rhapsody",
                artists = listOf(Artist("1", "Queen", "spotify:artist:1")),
                album = Album("1", "A Night at the Opera", null, "1975"),
                durationMs = 354000,
                popularity = 95,
                previewUrl = "https://preview.url",
                spotifyUri = "spotify:track:1",
                externalUrl = "https://open.spotify.com/track/1"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ActionButtonsPreview() {
    SongSwipeTheme {
        ActionButtons(
            onLike = {},
            onDislike = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CompletedContentPreview() {
    SongSwipeTheme {
        CompletedContent(
            likedCount = 15,
            totalCount = 50,
            onRestart = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorContentPreview() {
    SongSwipeTheme {
        ErrorContent(
            message = "Failed to load tracks. Please check your connection.",
            onRetry = {}
        )
    }
}
