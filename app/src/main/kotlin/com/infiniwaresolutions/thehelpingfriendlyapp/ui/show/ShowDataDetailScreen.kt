package com.infiniwaresolutions.thehelpingfriendlyapp.ui.show

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.infiniwaresolutions.thehelpingfriendlyapp.R
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.BuildPhishNetClickableUrlText
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.BuildSetlistAndFooterAnnotatedString
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.BuildSetlistNotes
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.LoadingIndicator
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.NoDataErrorText
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.PullToRefreshBox
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDataDetailScreen(
    showId: Int?,
    viewModel: ShowDataDetailViewModelCollection = hiltViewModel<ShowDataDetailViewModelCollection, ShowDataDetailViewModelCollection.DetailViewModelFactory> { factory ->
        factory.create(showId)
    }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.dotNetData.isNotEmpty() && !state.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = {
                    viewModel.sendIntent(ShowDataDetailIntent.GetSetlistById(showId = state.showId))
                }
            ) {
                Column {
                    // Artist name
                    state.dotNetData.first().artistName?.let {
                        Text(
                            modifier = Modifier
                                .padding(6.dp),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            text = it
                        )
                    }

                    // Show date
                    state.dotNetData.first().showDate?.let { date ->
                        // Format date from "yyyy-mm-dd" to "January 1, 2025"
                        val formattedDate =
                            LocalDate.parse(date)
                                .format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
                        Text(
                            modifier = Modifier
                                .padding(6.dp),
                            fontSize = 20.sp,
                            text = formattedDate
                        )
                    }

                    // Location
                    var stateOrCountry = state.dotNetData.first().state
                    if (stateOrCountry == "") stateOrCountry = state.dotNetData.first().country
                    Text(
                        modifier = Modifier
                            .padding(6.dp),
                        fontSize = 18.sp,
                        text = "${state.dotNetData.first().city}, $stateOrCountry",
                    )

                    // Soundcheck, setlist, and footnotes
                    BuildSetlistAndFooterAnnotatedString(
                        context = LocalContext.current,
                        dotNetData = state.dotNetData,
                        includeSoundcheck = true,
                        includeFootnotes = true
                    )

                    // Setlist notes
                    BuildSetlistNotes(
                        LocalContext.current,
                        state.dotNetData.first().setlistNotes.toString(),
                        true
                    )

                    // URL for phish.net webpage
                    state.dotNetData.first().permalink?.let {
                        BuildPhishNetClickableUrlText(
                            LocalContext.current, it
                        )
                    }
                }
            }
        }
    } else if (state.errorMessage?.isNotEmpty() == true || !state.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NoDataErrorText(stringResource(R.string.no_data_pull_refresh))
        }
    }

    // Overlay loading indicator
    if (state.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoadingIndicator()
        }
    }
}
