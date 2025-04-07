package com.infiniwaresolutions.thehelpingfriendlyapp.ui.show

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infiniwaresolutions.thehelpingfriendlyapp.data.DotNetSetlistData
import com.infiniwaresolutions.thehelpingfriendlyapp.data.DotNetSetlistSongData
import com.infiniwaresolutions.thehelpingfriendlyapp.data.Resource
import com.infiniwaresolutions.thehelpingfriendlyapp.domain.GetDotNetSetlistByShowIdUseCase
import com.infiniwaresolutions.thehelpingfriendlyapp.helpers.organizeDotNetSetlist
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.UIErrorType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed class ShowDataDetailIntent {
    data class GetSetlistById(val showId: Int?) : ShowDataDetailIntent()
}

data class ShowDataDetailViewState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val showId: Int? = null,
    val dotNetData: List<DotNetSetlistSongData> = listOf(),
    val errorMessage: UIErrorType = UIErrorType.None
)

@HiltViewModel(assistedFactory = ShowDataDetailViewModelCollection.DetailViewModelFactory::class)
class ShowDataDetailViewModelCollection @AssistedInject constructor(
    @Assisted val showId: Int?,
    private val getDotNetSetlistByShowIdUseCase: GetDotNetSetlistByShowIdUseCase,
) : ViewModel() {

    @AssistedFactory
    interface DetailViewModelFactory {
        fun create(showId: Int?): ShowDataDetailViewModelCollection
    }

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
                is Resource.Error -> _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = UIErrorType.Network
                    )
                }

                is Resource.Loading -> Unit
            }
        }
    }

    private fun <T> handleSuccess(data: T?) {
        _state.update { currentState ->
            when (data) {
                is DotNetSetlistData -> {
                    val sortedShowDataList: List<List<DotNetSetlistSongData>> =
                        organizeDotNetSetlist(data.dotNetSongEntities, true)
                    if (sortedShowDataList.isNotEmpty()) {
                        currentState.copy(
                            isLoading = false,
                            dotNetData = sortedShowDataList.first(),
                            errorMessage = UIErrorType.None
                        )
                    } else {
                        currentState.copy(
                            isLoading = false,
                            dotNetData = listOf(),
                            errorMessage = UIErrorType.NoData
                        )
                    }
                }

                else -> currentState.copy(isLoading = false, errorMessage = UIErrorType.Unknown)
            }
        }
    }
}
