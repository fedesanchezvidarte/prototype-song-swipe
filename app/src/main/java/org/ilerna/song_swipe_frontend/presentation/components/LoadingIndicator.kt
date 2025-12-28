package org.ilerna.song_swipe_frontend.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.ilerna.song_swipe_frontend.presentation.theme.ContentAlphaMedium
import org.ilerna.song_swipe_frontend.presentation.theme.NeonCyan
import org.ilerna.song_swipe_frontend.presentation.theme.Spacing

/**
 * Reusable loading indicator with optional message text.
 * Uses the app's neon color scheme.
 *
 * @param modifier Modifier for the container
 * @param message Optional loading message to display below the spinner
 * @param size Size of the progress indicator (default 48.dp)
 * @param fillMaxSize Whether to fill the available space (default true)
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    message: String? = null,
    size: Dp = 48.dp,
    fillMaxSize: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme
    
    Box(
        modifier = if (fillMaxSize) modifier.fillMaxSize() else modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(size),
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
                color = NeonCyan
            )
            
            if (message != null) {
                Spacer(modifier = Modifier.height(Spacing.spaceMd))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onBackground.copy(alpha = ContentAlphaMedium)
                )
            }
        }
    }
}
