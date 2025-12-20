package org.ilerna.song_swipe_frontend.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import org.ilerna.song_swipe_frontend.presentation.theme.NeonCyan
import org.ilerna.song_swipe_frontend.presentation.theme.NeonOrange
import org.ilerna.song_swipe_frontend.presentation.theme.NeonPink
import org.ilerna.song_swipe_frontend.presentation.theme.NeonPurple
import org.ilerna.song_swipe_frontend.presentation.theme.Sizes
import org.ilerna.song_swipe_frontend.presentation.theme.SongSwipeTheme
import org.ilerna.song_swipe_frontend.presentation.theme.Spacing

/**
 * Reusable category card component for displaying music genres/categories.
 * Similar to Spotify's Search screen cards.
 *
 * @param title The title/name of the category
 * @param backgroundColor The background color of the card
 * @param onClick Callback when the card is clicked
 * @param modifier Modifier for the card
 */
@Composable
fun CategoryCard(
    title: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(Spacing.spaceMd),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Color.White
        )
    }
}

/**
 * Gradient version of the category card with a gradient background.
 *
 * @param title The title/name of the category
 * @param gradientColors List of colors for the gradient
 * @param onClick Callback when the card is clicked
 * @param modifier Modifier for the card
 */
@Composable
fun GradientCategoryCard(
    title: String,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.linearGradient(colors = gradientColors)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
            .background(gradient)
            .clickable(onClick = onClick)
            .padding(Spacing.spaceMd),
        contentAlignment = Alignment.BottomStart
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Color.White
        )
    }
}

/* PREVIEWS */
@Preview(showBackground = true)
@Composable
fun PreviewCategoryCard() {
    SongSwipeTheme {
        CategoryCard(
            title = "Pop",
            backgroundColor = NeonPink,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGradientCategoryCard() {
    SongSwipeTheme {
        GradientCategoryCard(
            title = "Electronic",
            gradientColors = listOf(NeonPurple, NeonCyan),
            onClick = {}
        )
    }
}
