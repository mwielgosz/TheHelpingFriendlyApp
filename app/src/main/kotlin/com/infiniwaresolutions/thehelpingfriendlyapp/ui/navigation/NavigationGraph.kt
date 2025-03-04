package com.infiniwaresolutions.thehelpingfriendlyapp.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.infiniwaresolutions.thehelpingfriendlyapp.R
import com.infiniwaresolutions.thehelpingfriendlyapp.data.local.ShowData
import com.infiniwaresolutions.thehelpingfriendlyapp.data.network.DotNetShow
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.allShows.AllShowsScreen
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.setlist.SetlistScreen
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.show.ShowDataDetailScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    topAppBarTitle: (String) -> Unit,
    isBottomBarVisible: (Boolean) -> Unit,
    isBackButtonVisible: (Boolean) -> Unit,
    isSearchButtonVisible: (Boolean) -> Unit
) {
    NavHost(navController, startDestination = BottomNavItem.AllShowsNav.route) {
        composable(BottomNavItem.AllShowsNav.route) {
            topAppBarTitle(stringResource(R.string.all_shows))
            isBottomBarVisible(true)
            isBackButtonVisible(false)
            isSearchButtonVisible(false)
            AllShowsScreen(
                onShowCardClicked = { show ->
                    Log.d("NavGraph", "AllShows - Show card nav graph click")
                    navController.navigate(show)
                }
            )
        }

        // TODO: Try to merge these two with a route argument
        composable(BottomNavItem.SetlistsNav.route) {
            topAppBarTitle(stringResource(R.string.app_name))
            isBottomBarVisible(true)
            isBackButtonVisible(false)
            isSearchButtonVisible(true)
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                SetlistScreen(isSearch = false,
                    onShowCardClicked = { show ->
                        Log.d("NavGraph", "Setlist - Show card nav graph click")
                        navController.navigate(show)
                    }
                )
            }
        }

        composable(BottomNavItem.SearchNav.route) {
            topAppBarTitle(stringResource(R.string.search))
            isBottomBarVisible(true)
            isBackButtonVisible(false)
            isSearchButtonVisible(true)
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                SetlistScreen(isSearch = true,
                    onShowCardClicked = { show ->
                        Log.d("NavGraph", "Setlist - Show card nav graph click")
                        navController.navigate(show)
                    }
                )
            }
        }

        // TODO: Confirm data types into one (if possible from API)
        // From AllShows
        composable<DotNetShow> { backStackEntry ->
            val showData: DotNetShow = backStackEntry.toRoute()
            ShowDataDetailScreen(showData.showId)
        }

        // From Setlist
        composable<ShowData> { backStackEntry ->
            val showData: ShowData = backStackEntry.toRoute()
            ShowDataDetailScreen(showData.showId)
        }
        //composable(
        //    "${Routes.SetlistDetailRoute.route}/{showId}",
        //    arguments = listOf(navArgument("showId") { type = NavType.IntType })
        /*) { backStackEntry ->
            val showId = backStackEntry.arguments?.getInt("showId", 0)
            if (showId != 0) {
                Log.d("NavGraph", "Got ShowID: $showId")
                // Format date from "yyyy-dd-mm" to "M/d/yyyy"
                //val formattedDate = LocalDate.parse(showData.showDate.toString())
                //    .format(DateTimeFormatter.ofPattern("M/d/yyyy"))
                //topAppBarTitle("Setlist - $formattedDate")
                isBottomBarVisible(false)
                isBackButtonVisible(true)
                isSearchButtonVisible(false)
                topAppBarTitle(stringResource(R.string.setlist_info))
                ShowDataDetailScreen(showId = showId)
            }
        }*/

        /*composable<ShowData> { backStackEntry ->
            Log.d("NavGraph", "Got showData from SetlistScreen")
            val showData: ShowData = backStackEntry.toRoute<ShowData>()
            // Format date from "yyyy-dd-mm" to "M/d/yyyy"
            val formattedDate = LocalDate.parse(showData.showDate.toString())
                .format(DateTimeFormatter.ofPattern("M/d/yyyy"))
            topAppBarTitle("Setlist - $formattedDate")
            isBottomBarVisible(false)
            isBackButtonVisible(true)
            isSearchButtonVisible(false)
            //ShowDataDetailScreen(showData)
        }*/
    }
}
