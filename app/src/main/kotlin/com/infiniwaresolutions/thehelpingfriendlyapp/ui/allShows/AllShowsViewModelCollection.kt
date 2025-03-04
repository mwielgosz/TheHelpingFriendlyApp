package com.infiniwaresolutions.thehelpingfriendlyapp.ui.allShows

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infiniwaresolutions.thehelpingfriendlyapp.data.local.ShowData
import com.infiniwaresolutions.thehelpingfriendlyapp.data.network.DotNetShow
import com.infiniwaresolutions.thehelpingfriendlyapp.data.network.DotNetShowData
import com.infiniwaresolutions.thehelpingfriendlyapp.data.network.Resource
import com.infiniwaresolutions.thehelpingfriendlyapp.domain.GetAllDotNetShowsUseCase
import com.infiniwaresolutions.thehelpingfriendlyapp.domain.GetDotNetSetlistByShowIdUseCase
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
    data class GetSetlistById(val showId: Int?) : AllShowsIntent()
}

data class AllShowsViewState(
    val isLoading: Boolean = false,
    val allShowsData: List<DotNetShow> = emptyList(),
    val showData: List<ShowData> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class AllShowsViewModelCollection @Inject constructor(
    private val getAllDotNetShowsUseCase: GetAllDotNetShowsUseCase,
    private val getDotNetSetlistByShowIdUseCase: GetDotNetSetlistByShowIdUseCase
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
                    is AllShowsIntent.GetSetlistById -> executeSuspend {
                        _state.update { it.copy(isLoading = true) }
                        Log.d(
                            "AllShowsSViewModelCollection",
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
                        errorMessage = null
                    )
                }

                else -> currentState.copy(isLoading = false, errorMessage = "Unknown data type")
            }
        }
    }
}
