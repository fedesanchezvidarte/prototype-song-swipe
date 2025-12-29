package org.ilerna.song_swipe_frontend.presentation.model

import androidx.compose.ui.graphics.Color
import org.ilerna.song_swipe_frontend.domain.model.MusicCategory
import org.ilerna.song_swipe_frontend.presentation.theme.NeonCyan
import org.ilerna.song_swipe_frontend.presentation.theme.NeonOrange
import org.ilerna.song_swipe_frontend.presentation.theme.NeonPink
import org.ilerna.song_swipe_frontend.presentation.theme.NeonPurple

/**
 * UI model for MusicCategory with Compose Color.
 * Separates presentation layer from domain layer.
 */
data class MusicCategoryUi(
    val id: String,
    val name: String,
    val color: Color,
    val gradientColors: List<Color>? = null
)

/**
 * Maps domain MusicCategory to UI model with Compose Colors.
 * Uses predefined colors from Color.kt based on category ID.
 */
fun MusicCategory.toUi(): MusicCategoryUi {
    val (color, gradient) = getCategoryColors(id)
    return MusicCategoryUi(
        id = id,
        name = name,
        color = color,
        gradientColors = gradient
    )
}

/**
 * Maps list of domain categories to UI models.
 */
fun List<MusicCategory>.toUi(): List<MusicCategoryUi> = map { it.toUi() }

/**
 * Get predefined colors for a category based on its ID.
 * Colors are centralized in Color.kt for consistency.
 */
private fun getCategoryColors(categoryId: String): Pair<Color, List<Color>?> {
    return when (categoryId) {
        "1" -> NeonPink to null                              // Pop
        "2" -> NeonOrange to null                            // Rock
        "3" -> NeonCyan to listOf(NeonPurple, NeonCyan)      // Electronic
        "4" -> NeonPurple to null                            // Hip Hop
        "5" -> CategoryColors.Jazz to null                   // Jazz
        "6" -> CategoryColors.Classical to null              // Classical
        "7" -> NeonPink to listOf(NeonPink, NeonOrange)      // R&B
        "8" -> CategoryColors.Country to null                // Country
        "9" -> NeonOrange to listOf(NeonOrange, NeonPink)    // Latin
        "10" -> CategoryColors.Indie to null                 // Indie
        else -> NeonPink to null                             // Default
    }
}

/**
 * Additional category colors not defined in the main Color.kt theme.
 * These are specific to music categories.
 */
object CategoryColors {
    val Jazz = Color(0xFF1DB954)      // Spotify green
    val Classical = Color(0xFF8B5CF6) // Purple
    val Country = Color(0xFFD97706)   // Amber
    val Indie = Color(0xFF6366F1)     // Indigo
}
