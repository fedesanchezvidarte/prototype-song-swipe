package org.ilerna.song_swipe_frontend.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.ilerna.song_swipe_frontend.domain.model.User
import org.ilerna.song_swipe_frontend.presentation.components.CategoryCard
import org.ilerna.song_swipe_frontend.presentation.components.GradientCategoryCard
import org.ilerna.song_swipe_frontend.presentation.theme.NeonCyan
import org.ilerna.song_swipe_frontend.presentation.theme.NeonGradientColors
import org.ilerna.song_swipe_frontend.presentation.theme.NeonOrange
import org.ilerna.song_swipe_frontend.presentation.theme.NeonPink
import org.ilerna.song_swipe_frontend.presentation.theme.NeonPurple
import org.ilerna.song_swipe_frontend.presentation.theme.SongSwipeTheme
import org.ilerna.song_swipe_frontend.presentation.theme.Spacing

/**
 * Data class representing a music category for the home screen.
 */
data class MusicCategory(
    val id: String,
    val name: String,
    val color: Color,
    val gradientColors: List<Color>? = null
)

/**
 * Home Screen displaying user welcome message and category cards.
 *
 * @param user The current logged-in user
 * @param onCategoryClick Callback when a category card is clicked
 * @param modifier Modifier for the screen
 */
@Composable
fun HomeScreen(
    user: User?,
    onCategoryClick: (MusicCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    // Mock categories for the prototype
    val categories = listOf(
        MusicCategory("1", "Pop", NeonPink),
        MusicCategory("2", "Rock", NeonOrange),
        MusicCategory("3", "Electronic", NeonCyan, listOf(NeonPurple, NeonCyan)),
        MusicCategory("4", "Hip Hop", NeonPurple),
        MusicCategory("5", "Jazz", Color(0xFF1DB954)),
        MusicCategory("6", "Classical", Color(0xFF8B5CF6)),
        MusicCategory("7", "R&B", NeonPink, listOf(NeonPink, NeonOrange)),
        MusicCategory("8", "Country", Color(0xFFD97706)),
        MusicCategory("9", "Latin", NeonOrange, listOf(NeonOrange, NeonPink)),
        MusicCategory("10", "Indie", Color(0xFF6366F1))
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = Spacing.spaceMd)
    ) {
        Spacer(modifier = Modifier.height(Spacing.spaceXl))

        // User Header Section
        UserHeader(user = user)

        Spacer(modifier = Modifier.height(Spacing.spaceXl))

        // Section Title
        Text(
            text = "Explore Categories",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(Spacing.spaceMd))

        // Category Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(vertical = Spacing.spaceSm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.spaceMd),
            verticalArrangement = Arrangement.spacedBy(Spacing.spaceMd),
            modifier = Modifier.fillMaxSize()
        ) {
            items(categories) { category ->
                if (category.gradientColors != null) {
                    GradientCategoryCard(
                        title = category.name,
                        gradientColors = category.gradientColors,
                        onClick = { onCategoryClick(category) }
                    )
                } else {
                    CategoryCard(
                        title = category.name,
                        backgroundColor = category.color,
                        onClick = { onCategoryClick(category) }
                    )
                }
            }
        }
    }
}

/**
 * User header section with avatar and welcome message.
 */
@Composable
private fun UserHeader(
    user: User?,
    modifier: Modifier = Modifier
) {
    val gradientBorder = Brush.linearGradient(colors = NeonGradientColors)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circular Avatar with gradient border
        Box(
            modifier = Modifier
                .size(64.dp)
                .border(
                    width = 2.dp,
                    brush = gradientBorder,
                    shape = CircleShape
                )
                .padding(3.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (user?.profileImageUrl != null) {
                AsyncImage(
                    model = user.profileImageUrl,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback: Show first letter of display name
                Text(
                    text = user?.displayName?.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(Spacing.spaceMd))

        // Welcome message
        Column {
            Text(
                text = "Welcome back,",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            Text(
                text = user?.displayName ?: "Music Lover",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

/* PREVIEWS */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreen() {
    SongSwipeTheme {
        HomeScreen(
            user = User(
                id = "1",
                email = "user@example.com",
                displayName = "John Doe",
                profileImageUrl = null
            ),
            onCategoryClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreenNoUser() {
    SongSwipeTheme {
        HomeScreen(
            user = null,
            onCategoryClick = {}
        )
    }
}
