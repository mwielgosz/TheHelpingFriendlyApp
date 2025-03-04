package com.infiniwaresolutions.thehelpingfriendlyapp.ui.setlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infiniwaresolutions.thehelpingfriendlyapp.data.local.ShowData
import com.infiniwaresolutions.thehelpingfriendlyapp.data.network.DotNetSetlistData
import com.infiniwaresolutions.thehelpingfriendlyapp.data.network.Resource
import com.infiniwaresolutions.thehelpingfriendlyapp.domain.GetAllDotNetSetlistsByLimitUseCase
import com.infiniwaresolutions.thehelpingfriendlyapp.domain.GetAllDotNetSetlistsUseCase
import com.infiniwaresolutions.thehelpingfriendlyapp.domain.GetDotNetSearchByShowDateUseCase
import com.infiniwaresolutions.thehelpingfriendlyapp.helpers.organizeDataFromJson
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

sealed class SetlistIntent {
    data object GetAllSetlists : SetlistIntent()
    data class GetAllSetlistsByLimit(val limit: Int) : SetlistIntent()
    data class GetSetlistSearchByShowDate(val showDate: String) : SetlistIntent()
}

data class SetlistViewState(
    val isSearch: Boolean,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val showData: List<ShowData> = emptyList(),
    val showDate: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel(assistedFactory = SetlistViewModelCollection.ViewModelFactory::class)
class SetlistViewModelCollection @AssistedInject constructor(
    @Assisted val isSearch: Boolean,
    private val getAllDotNetSetlistsUseCase: GetAllDotNetSetlistsUseCase,
    private val getAllDotNetSetlistsByLimit: GetAllDotNetSetlistsByLimitUseCase,
    private val getDotNetSearchByShowDateUseCase: GetDotNetSearchByShowDateUseCase
) : ViewModel() {

    @AssistedFactory
    interface ViewModelFactory {
        fun create(isSearch: Boolean): SetlistViewModelCollection
    }

    private val _state = MutableStateFlow(SetlistViewState(isSearch = isSearch))
    val state: StateFlow<SetlistViewState> = _state

    private val _intentChannel = Channel<SetlistIntent>(Channel.UNLIMITED)

    init {
        if (isSearch &&  state.value.showDate != null) {
            sendIntent(SetlistIntent.GetSetlistSearchByShowDate(showDate = state.value.showDate!!))
        } else {
            sendIntent(SetlistIntent.GetAllSetlists)
        }
        handleIntents()
    }

    fun sendIntent(intent: SetlistIntent) {
        viewModelScope.launch {
            _intentChannel.send(intent)
        }
    }

    private fun handleIntents() {
        viewModelScope.launch {
            _intentChannel.consumeAsFlow().collect { intent ->
                when (intent) {
                    is SetlistIntent.GetAllSetlists -> executeSuspend { getAllDotNetSetlistsUseCase() }
                    is SetlistIntent.GetAllSetlistsByLimit -> executeSuspend {
                        getAllDotNetSetlistsByLimit(
                            intent.limit
                        )
                    }

                    is SetlistIntent.GetSetlistSearchByShowDate -> executeSuspend {
                        getDotNetSearchByShowDateUseCase(
                            intent.showDate
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
                /*is List<*> -> {
                    val sortedShowDataList: List<ShowData> = organizeDataFromJson(data.filterIsInstance<DotNetSongData>())
                    currentState.copy(isLoading = false, showData = sortedShowDataList, errorMessage = null)
                }*/
                else -> currentState.copy(isLoading = false, errorMessage = "Unknown data type")
            }
        }
    }
}
