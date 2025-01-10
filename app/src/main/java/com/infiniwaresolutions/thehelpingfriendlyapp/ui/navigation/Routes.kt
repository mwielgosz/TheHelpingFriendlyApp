package com.infiniwaresolutions.thehelpingfriendlyapp.ui.navigation

sealed class Routes(val route: String) {
    object HomeScreenRoute : Routes("homeScreen")
    object SearchScreenRoute : Routes("searchScreen")
    object ProfileScreenRoute : Routes("profileScreen")
}