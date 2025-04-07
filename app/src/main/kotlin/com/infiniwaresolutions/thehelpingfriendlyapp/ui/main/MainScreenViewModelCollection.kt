package com.infiniwaresolutions.thehelpingfriendlyapp.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infiniwaresolutions.thehelpingfriendlyapp.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/*val navController: NavHostController = rememberNavController()
var topBarTitle by remember { mutableStateOf(getString(R.string.app_name)) }
var bottomBarVisible by remember { mutableStateOf(true) }
var backButtonVisible by remember { mutableStateOf(false) }
var searchButtonVisible by remember { mutableStateOf(true) }
var searchFieldActive by remember { mutableStateOf(false) }
var searchBarInput by rememberSaveable { mutableStateOf("") }*/

sealed class MainScreenIntent {
    data class UpdateTopBarTitle(val title: String) : MainScreenIntent()
    data class UpdateBottomBarVisibility(val visible: Boolean) : MainScreenIntent()
    data class UpdateBackButtonVisibility(val visible: Boolean) : MainScreenIntent()
    data class UpdateSearchButtonVisibility(val visible: Boolean) : MainScreenIntent()
    data class UpdateSearchFieldActive(val active: Boolean) : MainScreenIntent()
    data class UpdateSearchBarInputText(val searchText: String) : MainScreenIntent()
}

data class MainScreenViewState(
    //val navController: NavHostController,
    val topBarTitle: String,
    val bottomBarVisibility: Boolean = true,
    val backButtonVisibility: Boolean = false,
    val searchButtonVisibility: Boolean = true,
    val searchFieldActive: Boolean = false,
    val searchBarInputText: String = ""
)

@HiltViewModel
class MainScreenViewModelCollection @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state =
        MutableStateFlow(MainScreenViewState(context.getString(R.string.app_name)))
    val state: StateFlow<MainScreenViewState> = _state

    private val _intentChannel = Channel<MainScreenIntent>(Channel.UNLIMITED)

    fun sendIntent(intent: MainScreenIntent) {
        viewModelScope.launch {
            _intentChannel.send(intent)
            handleIntents()
        }
    }

    private fun handleIntents() {
        viewModelScope.launch {
            _intentChannel.consumeAsFlow().collect { intent ->
                when (intent) {
                    is MainScreenIntent.UpdateTopBarTitle ->
                        _state.update { it.copy(topBarTitle = intent.title) }

                    is MainScreenIntent.UpdateBackButtonVisibility -> _state.update {
                        it.copy(backButtonVisibility = intent.visible)
                    }

                    is MainScreenIntent.UpdateBottomBarVisibility -> _state.update {
                        it.copy(bottomBarVisibility = intent.visible)
                    }

                    is MainScreenIntent.UpdateSearchButtonVisibility -> _state.update {
                        it.copy(searchButtonVisibility = intent.visible)
                    }

                    is MainScreenIntent.UpdateSearchFieldActive -> _state.update {
                        it.copy(searchFieldActive = intent.active)
                    }

                    is MainScreenIntent.UpdateSearchBarInputText -> _state.update {
                        it.copy(
                            searchBarInputText = intent.searchText
                        )
                    }
                }
            }
        }
    }
}
