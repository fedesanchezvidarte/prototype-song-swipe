package org.ilerna.song_swipe_frontend.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.ilerna.song_swipe_frontend.presentation.theme.OnGradient

/**
 * Circular icon button with solid color background.
 * Used for action buttons like dislike, skip, etc.
 *
 * @param icon Icon to display
 * @param contentDescription Accessibility description
 * @param onClick Callback when button is clicked
 * @param modifier Modifier for the button
 * @param backgroundColor Background color with alpha
 * @param iconColor Icon tint color
 * @param size Button size (default 72.dp)
 * @param iconSize Icon size (default 36.dp)
 */
@Composable
fun CircularIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    iconColor: Color,
    size: Dp = 72.dp,
    iconSize: Dp = 36.dp
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .background(
                color = backgroundColor,
                shape = CircleShape
            ),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = iconColor
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * Circular icon button with gradient background.
 * Used for primary action buttons like "like".
 *
 * @param icon Icon to display
 * @param contentDescription Accessibility description
 * @param onClick Callback when button is clicked
 * @param modifier Modifier for the button
 * @param gradientColors List of colors for the gradient background
 * @param iconColor Icon tint color (defaults to OnGradient/white)
 * @param size Button size (default 72.dp)
 * @param iconSize Icon size (default 36.dp)
 */
@Composable
fun GradientIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradientColors: List<Color>,
    iconColor: Color = OnGradient,
    size: Dp = 72.dp,
    iconSize: Dp = 36.dp
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .background(
                brush = Brush.linearGradient(colors = gradientColors),
                shape = CircleShape
            ),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = iconColor
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize)
        )
    }
}
