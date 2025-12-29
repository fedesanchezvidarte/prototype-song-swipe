package org.ilerna.song_swipe_frontend.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.ilerna.song_swipe_frontend.presentation.theme.ContentAlphaLow
import org.ilerna.song_swipe_frontend.presentation.theme.Spacing

/**
 * Compact button for inline actions (settings, dialogs, etc.)
 * Uses sober theme colors instead of gradient.
 *
 * @param text Button label text
 * @param onClick Callback when button is clicked
 * @param modifier Modifier for the button
 * @param containerColor Background color (defaults to surfaceVariant for sober look)
 * @param contentColor Text/icon color (defaults to onSurfaceVariant)
 * @param enabled Whether the button is enabled
 */
@Composable
fun CompactButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color? = null,
    contentColor: Color? = null,
    enabled: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme
    
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor ?: colorScheme.surfaceVariant,
            contentColor = contentColor ?: colorScheme.onSurfaceVariant,
            disabledContainerColor = colorScheme.surfaceVariant.copy(ContentAlphaLow),
            disabledContentColor = colorScheme.onSurfaceVariant.copy(ContentAlphaLow)
        ),
        contentPadding = PaddingValues(
            horizontal = Spacing.spaceMd,
            vertical = Spacing.spaceSm
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
