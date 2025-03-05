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
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.BuildSetlistNotes
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.LoadingIndicator
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.NoDataErrorText
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.PullToRefreshBox
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.buildSetlistAndFooterAnnotatedString
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

    val context = LocalContext.current

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
                    Text(
                        modifier = Modifier.padding(6.dp),
                        fontSize = 18.sp,
                        text = "${state.dotNetData.first().venue}\n${state.dotNetData.first().city}, ${state.dotNetData.first().state}\n"
                    )

                    // Soundcheck, setlist, and footnote builder
                    val setlistFooterBuilder = buildSetlistAndFooterAnnotatedString(
                        dotNetData = state.dotNetData,
                        includeSoundcheck = true,
                        includeFootnotes = true,
                    )

                    // Setlist
                    Text(
                        modifier = Modifier.padding(6.dp),
                        text = setlistFooterBuilder.first
                    )

                    // Footnotes
                    // Include 'footnote' meanings if enabled & present: "[1] This is a footer."
                    if (setlistFooterBuilder.second.isNotEmpty()) {
                        Text(
                            modifier = Modifier.padding(start = 6.dp, end = 6.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            text = "\n${context.resources.getString(R.string.setlist_footnotes)}"
                        )

                        Text(
                            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                            text = setlistFooterBuilder.second
                        )
                    }

                    // Setlist notes
                    BuildSetlistNotes(
                        LocalContext.current,
                        state.dotNetData.first().setlistNotes.toString(),
                        true
                    )
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
