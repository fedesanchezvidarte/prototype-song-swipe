package org.ilerna.song_swipe_frontend.presentation.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.ilerna.song_swipe_frontend.presentation.theme.SongSwipeTheme

/**
 * Bottom navigation bar component for the main screens.
 *
 * @param currentRoute The currently selected route
 * @param onItemClick Callback when a navigation item is clicked
 * @param modifier Modifier for the navigation bar
 */
@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onItemClick: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemClick(item) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

/* PREVIEWS */
@Preview(showBackground = true)
@Composable
fun PreviewBottomNavigationBar() {
    SongSwipeTheme {
        BottomNavigationBar(
            currentRoute = "home",
            onItemClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBottomNavigationBarPlaylists() {
    SongSwipeTheme {
        BottomNavigationBar(
            currentRoute = "playlists",
            onItemClick = {}
        )
    }
}
