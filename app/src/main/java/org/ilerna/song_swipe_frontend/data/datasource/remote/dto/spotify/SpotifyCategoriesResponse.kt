package org.ilerna.song_swipe_frontend.data.datasource.remote.dto.spotify

import com.google.gson.annotations.SerializedName

/**
 * DTO for the response of GET /browse/categories endpoint
 * Contains a paginated list of categories wrapped in a "categories" object
 */
data class SpotifyCategoriesResponse(
    @SerializedName("categories")
    val categories: SpotifyPaginatedCategoriesDto
)

/**
 * DTO for paginated categories list
 * Contains the items array and pagination metadata
 */
data class SpotifyPaginatedCategoriesDto(
    @SerializedName("items")
    val items: List<SpotifyCategoryDto>,

    @SerializedName("href")
    val href: String?,

    @SerializedName("limit")
    val limit: Int,

    @SerializedName("offset")
    val offset: Int,

    @SerializedName("total")
    val total: Int,

    @SerializedName("next")
    val next: String?,

    @SerializedName("previous")
    val previous: String?
)
