package org.ilerna.song_swipe_frontend.presentation.model

import androidx.compose.ui.graphics.Color
import org.ilerna.song_swipe_frontend.domain.model.MusicCategory
import org.ilerna.song_swipe_frontend.presentation.theme.NeonCyan
import org.ilerna.song_swipe_frontend.presentation.theme.NeonPink
import org.ilerna.song_swipe_frontend.presentation.theme.NeonPurple
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Unit tests for MusicCategoryUi mapper functions
 * Tests mapping from domain model to UI model with correct colors
 */
class MusicCategoryUiMapperTest {

    // ==================== Single Category Mapping Tests ====================

    @Test
    fun `toUi should map category with solid color`() {
        val category = MusicCategory("1", "Pop")
        val ui = category.toUi()

        assertEquals("1", ui.id)
        assertEquals("Pop", ui.name)
        assertEquals(NeonPink, ui.color)
        assertNull(ui.gradientColors)
    }

    @Test
    fun `toUi should map category with gradient`() {
        val category = MusicCategory("3", "Electronic")
        val ui = category.toUi()

        assertEquals("3", ui.id)
        assertEquals("Electronic", ui.name)
        assertEquals(NeonCyan, ui.color)
        assertNotNull(ui.gradientColors)
        assertEquals(listOf(NeonPurple, NeonCyan), ui.gradientColors)
    }

    @Test
    fun `toUi should use default color for unknown category ID`() {
        val category = MusicCategory("999", "Unknown Genre")
        val ui = category.toUi()

        assertEquals("999", ui.id)
        assertEquals("Unknown Genre", ui.name)
        assertEquals(NeonPink, ui.color) // Default color
        assertNull(ui.gradientColors)
    }

    // ==================== List Mapping Tests ====================

    @Test
    fun `List toUi should map all categories preserving order`() {
        val categories = listOf(
            MusicCategory("10", "Indie"),
            MusicCategory("5", "Jazz"),
            MusicCategory("1", "Pop")
        )

        val uiList = categories.toUi()

        assertEquals(3, uiList.size)
        assertEquals("Indie", uiList[0].name)
        assertEquals("Jazz", uiList[1].name)
        assertEquals("Pop", uiList[2].name)
    }

    @Test
    fun `List toUi should return empty list for empty input`() {
        val uiList = emptyList<MusicCategory>().toUi()
        assertEquals(0, uiList.size)
    }

    // ==================== CategoryColors Object Tests ====================

    @Test
    fun `CategoryColors should have expected values`() {
        assertEquals(Color(0xFF1DB954), CategoryColors.Jazz)
        assertEquals(Color(0xFF8B5CF6), CategoryColors.Classical)
        assertEquals(Color(0xFFD97706), CategoryColors.Country)
        assertEquals(Color(0xFF6366F1), CategoryColors.Indie)
    }
}
