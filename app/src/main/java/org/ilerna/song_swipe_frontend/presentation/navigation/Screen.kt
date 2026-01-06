package org.ilerna.song_swipe_frontend.presentation.navigation

/**
 * Sealed class representing all navigation destinations in the app.
 * Provides type-safe navigation routes.
 *
 * @property route The unique route string for navigation
 */
sealed class Screen(val route: String) {

    /**
     * Authentication flow screens
     */
    data object Login : Screen("login")

    /**
     * Main app screens (with bottom navigation)
     */
    data object Home : Screen("home")
    data object Playlists : Screen("playlists")
    data object Settings : Screen("settings")

    /**
     * Detail/nested screens
     */
    data object Swipe : Screen("swipe?playlistId={playlistId}") {
        /**
         * Creates a route with an optional playlist ID parameter.
         * @param playlistId The Spotify playlist ID to load, or null for default/featured playlist
         * @return The route string with the playlist ID parameter
         */
        fun createRoute(playlistId: String? = null): String {
            return if (playlistId != null) {
                "swipe?playlistId=$playlistId"
            } else {
                "swipe"
            }
        }

        /** Route pattern for navigation argument extraction */
        const val ROUTE_PATTERN = "swipe?playlistId={playlistId}"
        
        /** Argument key for the playlist ID */
        const val ARG_PLAYLIST_ID = "playlistId"
    }

    companion object {
        /**
         * List of screens that show the bottom navigation bar.
         */
        val bottomNavScreens = listOf(Home, Playlists, Settings)

        /**
         * Get Screen from route string.
         * @param route The route string to match
         * @return The matching Screen or null if not found
         */
        fun fromRoute(route: String?): Screen? {
            return when {
                route == null -> null
                route == Login.route -> Login
                route == Home.route -> Home
                route == Playlists.route -> Playlists
                route == Settings.route -> Settings
                route.startsWith("swipe") -> Swipe
                else -> null
            }
        }
    }
}
