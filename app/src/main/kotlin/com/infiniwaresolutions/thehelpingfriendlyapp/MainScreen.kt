package com.infiniwaresolutions.thehelpingfriendlyapp

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.isDateValidAndFormat
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.navigation.BottomNavBar
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.navigation.NavigationGraph
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.navigation.Routes
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.search.AppBarSearchField
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.theme.DarkColorScheme
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.theme.LightColorScheme
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.theme.PurpleMain
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.theme.Typography
import dagger.hilt.android.AndroidEntryPoint

const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TheHelpingFriendlyAppTheme {
                val navController: NavHostController = rememberNavController()
                var topBarTitle by remember { mutableStateOf(getString(R.string.app_name)) }
                var bottomBarVisible by remember { mutableStateOf(true) }
                var backButtonVisible by remember { mutableStateOf(false) }
                var searchButtonVisible by remember { mutableStateOf(true) }
                var searchFieldActive by remember { mutableStateOf(false) }
                var searchBarInput by rememberSaveable { mutableStateOf("") }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            navigationIcon = {
                                if (backButtonVisible) {
                                    IconButton(onClick = { navController.navigateUp() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = getString(R.string.back)
                                        )
                                    }
                                } else {
                                    Image(
                                        painter = painterResource(R.mipmap.ic_launcher_foreground),
                                        contentDescription = getString(R.string.app_icon)
                                    )
                                }
                            },
                            title = {
                                if (searchFieldActive) {
                                    val context = LocalContext.current
                                    AppBarSearchField(
                                        value = searchBarInput,
                                        onValueChange = { newValue -> searchBarInput = newValue },
                                        hint = getString(R.string.search_setlists_by_date),
                                        keyboardOptions = KeyboardOptions(
                                            imeAction = ImeAction.Search,
                                            keyboardType = KeyboardType.Number
                                        ),
                                        keyboardActions = KeyboardActions(onSearch = {
                                            val validDate = isDateValidAndFormat(searchBarInput)
                                            if (validDate == null) {
                                                Toast.makeText(
                                                    context,
                                                    "Invalid date format",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                searchFieldActive = false
                                                navController.navigate(Routes.SearchScreenRoute.route + "/$validDate")
                                                searchBarInput = ""
                                            }
                                        })
                                    )
                                } else {
                                    //Text(text = navController.currentDestination?.label.toString())
                                    Text(text = topBarTitle)
                                }
                            },
                            actions = {
                                if (searchButtonVisible) {
                                    IconButton(onClick = {
                                        // Search action button pressed by user
                                        searchFieldActive = !searchFieldActive
                                        searchBarInput = ""
                                    }) {
                                        Icon(
                                            Icons.Filled.Search,
                                            contentDescription = getString(R.string.search_icon)
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
                        if (bottomBarVisible) {
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
                                topBarTitle = title
                            },
                            isBottomBarVisible = { isBottomVisible ->
                                bottomBarVisible = isBottomVisible
                            },
                            isBackButtonVisible = { isBackVisible ->
                                backButtonVisible = isBackVisible
                            },
                            isSearchButtonVisible = { isSearchVisible ->
                                searchButtonVisible = isSearchVisible
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TheHelpingFriendlyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
