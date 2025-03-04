package com.infiniwaresolutions.thehelpingfriendlyapp.ui.allShows

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.infiniwaresolutions.thehelpingfriendlyapp.data.network.DotNetShow
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.BuildSetlistNotes
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AllShowsScreen(
    viewModel: AllShowsViewModelCollection = hiltViewModel(),
    onShowCardClicked: (Int?) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.allShowsData.isNotEmpty() == true && !state.isLoading) {
        ShowCardList(
            state = state,
            onCardClicked = { show ->
                Log.d("AllShowsScreen", "Show card clicked for id: ${show.showId}")
                viewModel.sendIntent(AllShowsIntent.GetSetlistById(show.showId))
                onShowCardClicked(show.showId)
            }
        )
    } else if (!state.isLoading || state.errorMessage?.isNotEmpty() == true) {
        Text(
            text = stringResource(R.string.no_data_pull_refresh),
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center),
            //style = MaterialTheme.typography.bodyMedium,
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
fun ShowCardList(
    state: AllShowsViewState,
    onCardClicked: (DotNetShow) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(state.allShowsData) { allShowsData ->
            ShowCard(
                dotNetShow = allShowsData,
                onCardClicked = { show ->
                    onCardClicked(show)
                }
            )
        }
    }
}

@Composable
fun ShowCard(
    dotNetShow: DotNetShow,
    onCardClicked: (DotNetShow) -> Unit,
) {
    Card(modifier = Modifier
        .clickable { onCardClicked(dotNetShow) },
        border = BorderStroke(1.dp, Color.Gray),
        elevation = CardDefaults.cardElevation(4.dp),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {

                    // Artist name
                    dotNetShow.artistName?.let {
                        Text(
                            modifier = Modifier
                                .padding(6.dp),
                            fontWeight = FontWeight.Bold,
                            text = it
                        )
                    }

                    // Horizontal space
                    Spacer(Modifier.weight(1f))

                    // Show date
                    dotNetShow.showDate?.let {
                        // Format date from "yyyy-mm-dd" to "January 1, 2025"
                        val date = LocalDate.parse(it)
                        val formattedDate = date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
                        Text(
                            modifier = Modifier
                                .padding(6.dp),
                            text = formattedDate
                        )
                    }
                }

                // Location
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "${dotNetShow.venue}"
                    )

                    // Horizontal space
                    Spacer(Modifier.weight(1f))

                    var stateOrCountry = dotNetShow.state
                    if (stateOrCountry == "") stateOrCountry = dotNetShow.country
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "${dotNetShow.city}, $stateOrCountry"
                    )
                }

                // Setlist notes
                BuildSetlistNotes(LocalContext.current, dotNetShow.setlistNotes.toString(), false)
            }
        }
    )
}
