package com.infiniwaresolutions.thehelpingfriendlyapp.ui.main

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.content.ContextCompat.getString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.infiniwaresolutions.thehelpingfriendlyapp.R
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.isDateValidAndFormat
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.navigation.BottomNavBar
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.navigation.NavigationGraph
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.navigation.Routes
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.search.AppBarSearchField
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.theme.PurpleMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainScreenViewModelCollection = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (state.backButtonVisibility) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = getString(LocalContext.current, R.string.back)
                            )
                        }
                    } else {
                        Image(
                            painter = painterResource(R.mipmap.ic_launcher_foreground),
                            contentDescription = getString(LocalContext.current, R.string.app_icon)
                        )
                    }
                },
                title = {
                    if (state.searchFieldActive) {
                        val context = LocalContext.current
                        AppBarSearchField(
                            value = state.searchBarInputText,
                            onValueChange = { newValue ->
                                viewModel.sendIntent(
                                    MainScreenIntent.UpdateSearchBarInputText(
                                        newValue
                                    )
                                )
                            },
                            hint = getString(
                                LocalContext.current,
                                R.string.search_setlists_by_date
                            ),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Search,
                                keyboardType = KeyboardType.Number
                            ),
                            keyboardActions = KeyboardActions(onSearch = {
                                val validDate = isDateValidAndFormat(state.searchBarInputText)
                                if (validDate == null) {
                                    Toast.makeText(
                                        context,
                                        "Invalid date format",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    viewModel.sendIntent(MainScreenIntent.UpdateSearchFieldActive(false))
                                    //state.searchFieldActive = false
                                    navController.navigate(Routes.SearchScreenRoute.route + "/$validDate")
                                    viewModel.sendIntent(MainScreenIntent.UpdateSearchBarInputText(""))
                                }
                            })
                        )
                    } else {
                        //Text(text = navController.currentDestination?.label.toString())
                        Text(text = state.topBarTitle)
                    }
                },
                actions = {
                    if (state.searchButtonVisibility) {
                        IconButton(onClick = {
                            // Search action button pressed by user
                            viewModel.sendIntent(MainScreenIntent.UpdateSearchFieldActive(!state.searchFieldActive))
                            viewModel.sendIntent(MainScreenIntent.UpdateSearchBarInputText(""))
                        }) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = getString(
                                    LocalContext.current,
                                    R.string.search_icon
                                )
                            )
                        }
                    }
                },
                colors = TopAppBarColors(
                    containerColor = PurpleMain,
                    scrolledContainerColor = Color.White,
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            if (state.bottomBarVisibility) {
                BottomNavBar(
                    navController = navController,
                    modifier = Modifier
                )
            }
        }) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            NavigationGraph(
                navController = navController,
                topAppBarTitle = { title ->
                    viewModel.sendIntent(MainScreenIntent.UpdateTopBarTitle(title))
                },
                isBottomBarVisible = { visible ->
                    viewModel.sendIntent(MainScreenIntent.UpdateBottomBarVisibility(visible))
                },
                isBackButtonVisible = { visible ->
                    viewModel.sendIntent(MainScreenIntent.UpdateBackButtonVisibility(visible))
                },
                isSearchButtonVisible = { visible ->
                    viewModel.sendIntent(MainScreenIntent.UpdateSearchButtonVisibility(visible))
                }
            )
        }
    }
}