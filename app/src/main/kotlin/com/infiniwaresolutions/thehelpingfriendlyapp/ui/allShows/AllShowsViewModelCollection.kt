package com.infiniwaresolutions.thehelpingfriendlyapp.ui.allShows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infiniwaresolutions.thehelpingfriendlyapp.data.models.DotNetShow
import com.infiniwaresolutions.thehelpingfriendlyapp.data.models.DotNetShowData
import com.infiniwaresolutions.thehelpingfriendlyapp.data.remote.Resource
import com.infiniwaresolutions.thehelpingfriendlyapp.domain.GetAllDotNetShowsUseCase
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.UIErrorType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed class AllShowsIntent {
    data object GetAllShows : AllShowsIntent()
}

data class AllShowsViewState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val allShowsData: List<DotNetShow> = emptyList(),
    val errorMessage: UIErrorType = UIErrorType.None
)

@HiltViewModel
class AllShowsViewModelCollection @Inject constructor(
    private val getAllDotNetShowsUseCase: GetAllDotNetShowsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(AllShowsViewState())
    val state: StateFlow<AllShowsViewState> = _state

    private val _intentChannel = Channel<AllShowsIntent>(Channel.UNLIMITED)

    init {
        sendIntent(AllShowsIntent.GetAllShows)
        handleIntents()
    }

    fun sendIntent(intent: AllShowsIntent) {
        viewModelScope.launch {
            _intentChannel.send(intent)
        }
    }

    private fun handleIntents() {
        viewModelScope.launch {
            _intentChannel.consumeAsFlow().collect { intent ->
                when (intent) {
                    is AllShowsIntent.GetAllShows -> executeSuspend { getAllDotNetShowsUseCase() }
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
                is DotNetShowData -> {
                    // Filter shows that have happened in the past
                    val allShowData: List<DotNetShow> = data.dotNetShowData
                        .filter {
                            LocalDate.now().isAfter(
                                LocalDate.parse(
                                    it.showDate,
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                )
                            )
                        }
                    currentState.copy(
                        isLoading = false,
                        allShowsData = allShowData,
                        errorMessage = UIErrorType.None
                    )
                }

                else -> currentState.copy(isLoading = false, errorMessage = UIErrorType.Unknown)
            }
        }
    }
}
