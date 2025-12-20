package org.ilerna.song_swipe_frontend.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * SettingItemWithDropdown - Generic reusable component for a setting with dropdown selector.
 * Can be used for any type of option selection (themes, languages, view modes, etc.)
 * 
 * @param T The type of the options
 * @param title The title of the setting
 * @param description The description of the setting
 * @param selectedOption The currently selected option
 * @param options List of all available options
 * @param optionLabel Lambda to get the display label for each option
 * @param onOptionSelected Callback when an option is selected
 * @param contentDescription Content description for accessibility
 * @param modifier Optional modifier
 */
@Composable
fun <T> SettingItemWithDropdown(
    title: String,
    description: String,
    selectedOption: T,
    options: List<T>,
    optionLabel: (T) -> String,
    onOptionSelected: (T) -> Unit,
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
                text = optionLabel(selectedOption),
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
                            text = optionLabel(option),
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
