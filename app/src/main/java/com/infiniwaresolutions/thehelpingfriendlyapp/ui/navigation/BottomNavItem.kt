package com.infiniwaresolutions.thehelpingfriendlyapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    data object AllShowsNav :
        BottomNavItem(Routes.AllShowsScreenRoute.route, Icons.Default.Person, "All Shows")

    data object SetlistsNav :
        BottomNavItem(Routes.SetlistScreenRoute.route, Icons.AutoMirrored.Filled.List, "Setlists")

    data object SearchNav :
        BottomNavItem(Routes.SearchScreenRoute.route, Icons.Default.Search, "Search")
}
