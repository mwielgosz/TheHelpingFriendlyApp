package com.infiniwaresolutions.thehelpingfriendlyapp.ui.show

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infiniwaresolutions.thehelpingfriendlyapp.data.local.ShowData
import com.infiniwaresolutions.thehelpingfriendlyapp.data.network.DotNetSetlistData
import com.infiniwaresolutions.thehelpingfriendlyapp.data.network.Resource
import com.infiniwaresolutions.thehelpingfriendlyapp.domain.GetDotNetSetlistByShowIdUseCase
import com.infiniwaresolutions.thehelpingfriendlyapp.helpers.organizeDataFromJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class ShowDataDetailIntent {
    data class GetSetlistById(val showId: Int?) : ShowDataDetailIntent()
}

data class ShowDataDetailViewState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val showId: Int? = null,
    val showData: List<ShowData> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class ShowDataDetailViewModelCollection @Inject constructor(
    private val getDotNetSetlistByShowIdUseCase: GetDotNetSetlistByShowIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val showId = savedStateHandle.get<Int>("showId")
    private val _state = MutableStateFlow(ShowDataDetailViewState())
    val state: StateFlow<ShowDataDetailViewState> = _state

    private val _intentChannel = Channel<ShowDataDetailIntent>(Channel.UNLIMITED)

    init {
        sendIntent(ShowDataDetailIntent.GetSetlistById(showId = showId))
        handleIntents()
    }

    fun sendIntent(intent: ShowDataDetailIntent) {
        viewModelScope.launch {
            _intentChannel.send(intent)
        }
    }

    private fun handleIntents() {
        viewModelScope.launch {
            _intentChannel.consumeAsFlow().collect { intent ->
                when (intent) {
                    is ShowDataDetailIntent.GetSetlistById -> executeSuspend {
                        _state.update { it.copy(isLoading = true) }
                        Log.d(
                            "ShowDataDetailCollection",
                            "Intent received for show id: ${intent.showId}"
                        )
                        getDotNetSetlistByShowIdUseCase(
                            intent.showId
                        )
                    }
                }
            }
        }
    }

    private fun <T> executeSuspend(block: suspend () -> Resource<T>) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = block()) {
                is Resource.Success -> handleSuccess(result.data)
                is Resource.Error -> _state.update { it.copy(isLoading = false) }
                is Resource.Loading -> Unit
            }
        }
    }

    private fun <T> handleSuccess(data: T?) {
        _state.update { currentState ->
            when (data) {
                is DotNetSetlistData -> {
                    val sortedShowDataList: List<ShowData> =
                        organizeDataFromJson(data.dotNetSongEntities)
                    currentState.copy(
                        isLoading = false,
                        showData = sortedShowDataList,
                        errorMessage = null
                    )
                }

                else -> currentState.copy(isLoading = false, errorMessage = "Unknown data type")
            }
        }
    }
}
