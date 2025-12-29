package org.ilerna.song_swipe_frontend.data.repository.impl

import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for CategoryRepositoryImpl
 * Tests the mock data provider for music categories
 */
class CategoryRepositoryImplTest {

    private lateinit var repository: CategoryRepositoryImpl

    @Before
    fun setUp() {
        repository = CategoryRepositoryImpl()
    }

    // ==================== getCategories Tests ====================

    @Test
    fun `getCategories should return all mock categories`() = runTest {
        val result = repository.getCategories()

        assertTrue(result.isSuccess)
        val categories = result.getOrNull()
        assertNotNull(categories)
        assertEquals(10, categories.size)
    }

    @Test
    fun `getCategories should return expected category names`() = runTest {
        val categories = repository.getCategories().getOrNull()!!
        val expectedNames = listOf(
            "Pop", "Rock", "Electronic", "Hip Hop", "Jazz",
            "Classical", "R&B", "Country", "Latin", "Indie"
        )

        assertEquals(expectedNames, categories.map { it.name })
    }

    // ==================== getCategoryById Tests ====================

    @Test
    fun `getCategoryById should return category for valid ID`() = runTest {
        val result = repository.getCategoryById("1")
        val category = result.getOrNull()

        assertNotNull(category)
        assertEquals("1", category.id)
        assertEquals("Pop", category.name)
    }

    @Test
    fun `getCategoryById should return null for unknown ID`() = runTest {
        val result = repository.getCategoryById("999")

        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    // ==================== Data Consistency Test ====================

    @Test
    fun `getCategoryById results should match getCategories results`() = runTest {
        val allCategories = repository.getCategories().getOrNull()!!

        allCategories.forEach { expected ->
            val byId = repository.getCategoryById(expected.id).getOrNull()
            assertNotNull(byId)
            assertEquals(expected.id, byId.id)
            assertEquals(expected.name, byId.name)
        }
    }
}
