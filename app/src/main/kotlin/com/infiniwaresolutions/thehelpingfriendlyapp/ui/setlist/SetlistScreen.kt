package com.infiniwaresolutions.thehelpingfriendlyapp.ui.setlist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.infiniwaresolutions.thehelpingfriendlyapp.data.local.ShowData
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.PullToRefreshBox
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.buildSetlistAnnotatedString
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetlistScreen(
    viewModel: SetlistViewModelCollection = hiltViewModel(),
    onShowCardClicked: (Int?) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.showData.isNotEmpty() && !state.isLoading) {
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.sendIntent(SetlistIntent.GetAllSetlists) },
        ) {
            SetlistCardList(
                state = state,
                onCardClicked = { show ->
                    onShowCardClicked(show.showId)
                }
            )
        }
    } else if (!state.isLoading || state.errorMessage?.isNotEmpty() == true) {
        Text(
            text = stringResource(R.string.no_data_pull_refresh),
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }

    // Overlay loading indicator
    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun SetlistCardList(
    state: SetlistViewState,
    onCardClicked: (ShowData) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(state.showData) { showData ->
            SetlistCard(
                showData = showData,
                onCardClicked = { show ->
                    onCardClicked(show)
                }
            )
        }
    }
}

@Composable
fun SetlistCard(
    showData: ShowData,
    onCardClicked: (ShowData) -> Unit,
) {
    Card(modifier = Modifier
        .clickable { onCardClicked(showData) },
        border = BorderStroke(1.dp, Color.Gray),
        elevation = CardDefaults.cardElevation(4.dp),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Artist name
                    showData.artistName?.let {
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
                    showData.showDate?.let {
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
                        text = "${showData.venue}"
                    )

                    var stateOrCountry = showData.state
                    if (stateOrCountry == "") stateOrCountry = showData.country
                    Text(
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f)
                            .wrapContentWidth(Alignment.End),
                        textAlign = TextAlign.End,
                        text = "${showData.city}, $stateOrCountry",
                    )
                }

                // Build string with all setlist data
                val songsStr = buildSetlistAnnotatedString(
                    context = LocalContext.current,
                    showData = showData,
                    includeSoundcheck = true,
                    includeFooter = false
                )

                // Setlist, soundcheck & footer
                Text(
                    modifier = Modifier.padding(6.dp),
                    text = songsStr
                )
            }
        }
    )
}
