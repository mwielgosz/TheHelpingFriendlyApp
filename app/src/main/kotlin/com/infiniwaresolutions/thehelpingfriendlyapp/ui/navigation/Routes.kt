package com.infiniwaresolutions.thehelpingfriendlyapp.ui.navigation

sealed class Routes(val route: String) {
    data object AllShowsScreenRoute : Routes("allShowsScreen")
    data object SetlistScreenRoute : Routes("setlistScreen")
    data object SearchScreenRoute : Routes("searchScreen")
}
