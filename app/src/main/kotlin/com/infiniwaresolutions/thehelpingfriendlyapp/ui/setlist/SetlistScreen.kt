package com.infiniwaresolutions.thehelpingfriendlyapp.ui.setlist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.infiniwaresolutions.thehelpingfriendlyapp.R
import com.infiniwaresolutions.thehelpingfriendlyapp.data.models.DotNetSetlistSongData
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.BuildSetlistAndFooterAnnotatedString
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.ErrorTextWithButton
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.LoadingIndicator
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.PullToRefreshBox
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.UIErrorType
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetlistScreen(
    isSearch: Boolean,
    showDate: String,
    viewModel: SetlistViewModelCollection = hiltViewModel<SetlistViewModelCollection, SetlistViewModelCollection.ViewModelFactory> { factory ->
        factory.create(isSearch, showDate)
    },
    onShowCardClicked: (DotNetSetlistSongData) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { viewModel.sendIntent(SetlistIntent.GetAllSetlists) },
    ) {

        if (state.dotNetSetlistSongData.isNotEmpty() && !state.isLoading) {
            SetlistCardList(
                state = state,
                onCardClicked = { show ->
                    onShowCardClicked(show)
                }
            )
        } else if (state.errorMessage == UIErrorType.Network && !state.isLoading) {
            // Error - Network
            ErrorTextWithButton(
                stringResource(R.string.no_data_network_connectivity),
                stringResource(R.string.refresh)
            ) { viewModel.sendIntent(SetlistIntent.GetAllSetlists) }
        } else if (state.isLoading) {
            // Overlay loading indicator
            LoadingIndicator()
        } else {
            // Fallback error
            ErrorTextWithButton(
                stringResource(R.string.no_data),
                stringResource(R.string.refresh)
            ) { viewModel.sendIntent(SetlistIntent.GetAllSetlists) }
        }
    }
}

@Composable
fun SetlistCardList(
    state: SetlistViewState,
    onCardClicked: (DotNetSetlistSongData) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(state.dotNetSetlistSongData) { dotNetData ->
            SetlistCard(
                dotNetData = dotNetData,
                onCardClicked = { show ->
                    onCardClicked(show)
                }
            )
        }
    }
}

@Composable
fun SetlistCard(
    dotNetData: List<DotNetSetlistSongData>,
    onCardClicked: (DotNetSetlistSongData) -> Unit,
) {
    Card(
        modifier = Modifier
            .clickable { onCardClicked(dotNetData.first()) },
        border = BorderStroke(1.dp, Color.Gray),
        elevation = CardDefaults.cardElevation(4.dp),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Artist name
                    dotNetData.first().artistName?.let {
                        Text(
                            modifier = Modifier
                                .padding(6.dp)
                                .weight(1f)
                                .wrapContentWidth(Alignment.Start),
                            fontWeight = FontWeight.Bold,
                            text = it
                        )
                    }

                    // Show date
                    dotNetData.first().showDate?.let {
                        // Format date from "yyyy-mm-dd" to "January 1, 2025"
                        val date = LocalDate.parse(it)
                        val formattedDate = date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                        Text(
                            modifier = Modifier
                                .padding(6.dp)
                                .weight(1f)
                                .wrapContentWidth(Alignment.End),
                            text = formattedDate
                        )
                    }
                }

                // Location
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start),
                        text = "${dotNetData.first().venue}"
                    )

                    var stateOrCountry = dotNetData.first().state
                    if (stateOrCountry == "") stateOrCountry = dotNetData.first().country
                    Text(
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f)
                            .wrapContentWidth(Alignment.End),
                        textAlign = TextAlign.End,
                        text = "${dotNetData.first().city}, $stateOrCountry",
                    )
                }

                // Soundcheck, setlist, and footnotes
                BuildSetlistAndFooterAnnotatedString(
                    context = LocalContext.current,
                    dotNetData = dotNetData,
                    includeSoundcheck = true,
                    includeFootnotes = false
                )
            }
        }
    )
}
