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
    data object Swipe : Screen("swipe")

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
            return when (route) {
                Login.route -> Login
                Home.route -> Home
                Playlists.route -> Playlists
                Settings.route -> Settings
                Swipe.route -> Swipe
                else -> null
            }
        }
    }
}
