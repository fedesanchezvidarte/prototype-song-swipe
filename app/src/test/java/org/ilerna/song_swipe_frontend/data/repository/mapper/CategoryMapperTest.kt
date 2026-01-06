package org.ilerna.song_swipe_frontend.data.repository.mapper

import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyCategoryDto
import org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify.SpotifyImageDto
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for CategoryMapper
 * Tests the mapping from SpotifyCategoryDto to MusicCategory domain model
 */
class CategoryMapperTest {

    // ==================== toDomain Tests ====================

    @Test
    fun `toDomain should map id correctly`() {
        // Given
        val dto = createCategoryDto(id = "pop")

        // When
        val result = CategoryMapper.toDomain(dto)

        // Then
        assertEquals("pop", result.id)
    }

    @Test
    fun `toDomain should map name correctly`() {
        // Given
        val dto = createCategoryDto(name = "Pop Music")

        // When
        val result = CategoryMapper.toDomain(dto)

        // Then
        assertEquals("Pop Music", result.name)
    }

    @Test
    fun `toDomain should map first icon URL when icons exist`() {
        // Given
        val icons = listOf(
            SpotifyImageDto("https://large.jpg", 300, 300),
            SpotifyImageDto("https://medium.jpg", 200, 200),
            SpotifyImageDto("https://small.jpg", 100, 100)
        )
        val dto = createCategoryDto(icons = icons)

        // When
        val result = CategoryMapper.toDomain(dto)

        // Then
        assertEquals("https://large.jpg", result.iconUrl)
    }

    @Test
    fun `toDomain should return null iconUrl when icons list is empty`() {
        // Given
        val dto = createCategoryDto(icons = emptyList())

        // When
        val result = CategoryMapper.toDomain(dto)

        // Then
        assertNull(result.iconUrl)
    }

    @Test
    fun `toDomain should return null iconUrl when icons is null`() {
        // Given
        val dto = createCategoryDto(icons = null)

        // When
        val result = CategoryMapper.toDomain(dto)

        // Then
        assertNull(result.iconUrl)
    }

    // ==================== toDomainList Tests ====================

    @Test
    fun `toDomainList should map all categories`() {
        // Given
        val dtos = listOf(
            createCategoryDto(id = "pop", name = "Pop"),
            createCategoryDto(id = "rock", name = "Rock"),
            createCategoryDto(id = "jazz", name = "Jazz")
        )

        // When
        val result = CategoryMapper.toDomainList(dtos)

        // Then
        assertEquals(3, result.size)
        assertEquals("pop", result[0].id)
        assertEquals("rock", result[1].id)
        assertEquals("jazz", result[2].id)
    }

    @Test
    fun `toDomainList should return empty list for empty input`() {
        // Given
        val dtos = emptyList<SpotifyCategoryDto>()

        // When
        val result = CategoryMapper.toDomainList(dtos)

        // Then
        assertEquals(0, result.size)
    }

    // ==================== Helper Functions ====================

    private fun createCategoryDto(
        id: String = "category_id",
        name: String = "Category Name",
        icons: List<SpotifyImageDto>? = null
    ): SpotifyCategoryDto {
        return SpotifyCategoryDto(
            id = id,
            name = name,
            href = "https://api.spotify.com/v1/browse/categories/$id",
            icons = icons
        )
    }
}
