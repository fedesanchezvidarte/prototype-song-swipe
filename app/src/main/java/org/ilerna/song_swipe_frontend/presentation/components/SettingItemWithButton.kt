package org.ilerna.song_swipe_frontend.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.ilerna.song_swipe_frontend.presentation.theme.ContentAlphaMedium

/**
 * SettingItemWithButton - Reusable component for a setting with a button action.
 * Follows the same design pattern as SettingItemWithDropdown.
 * 
 * @param title The title of the setting
 * @param description The description of the setting
 * @param buttonText Text to display on the button
 * @param onButtonClick Callback when the button is clicked
 * @param modifier Optional modifier
 */
@Composable
fun SettingItemWithButton(
    title: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme

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
                color = colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurface.copy(alpha = ContentAlphaMedium)
            )
        }

        // Action Button using CompactButton
        CompactButton(
            text = buttonText,
            onClick = onButtonClick,
            enabled = enabled
        )
    }
}
