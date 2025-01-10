package com.infiniwaresolutions.thehelpingfriendlyapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem(Routes.HomeScreenRoute.route, Icons.Default.Home, "Home")
    object Search : BottomNavItem(Routes.SearchScreenRoute.route, Icons.Default.Search, "Search")
    object Profile : BottomNavItem(Routes.ProfileScreenRoute.route, Icons.Default.Person, "Profile")
}