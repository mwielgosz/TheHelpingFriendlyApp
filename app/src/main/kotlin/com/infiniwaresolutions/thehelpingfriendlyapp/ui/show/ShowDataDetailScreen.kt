package com.infiniwaresolutions.thehelpingfriendlyapp.ui.show

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.infiniwaresolutions.thehelpingfriendlyapp.R
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.BuildSetlistNotes
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.PullToRefreshBox
import com.infiniwaresolutions.thehelpingfriendlyapp.ui.buildSetlistAnnotatedString
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDataDetailScreen(
    viewModel: ShowDataDetailViewModelCollection = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        val context = LocalContext.current

        if (state.showData.isNotEmpty() && !state.isLoading) {
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = {
                    viewModel.sendIntent(ShowDataDetailIntent.GetSetlistById(showId = state.showId))
                }
            ) {
                Column {
                    // Artist name
                    state.showData.first().artistName?.let {
                        Text(
                            modifier = Modifier
                                .padding(6.dp),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            text = it
                        )
                    }

                    // Show date
                    state.showData.first().showDate?.let { date ->
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
                        text = "${state.showData.first().venue}\n${state.showData.first().city}, ${state.showData.first().state}\n"
                    )

                    // Build string with all setlist data
                    val songsStr = buildSetlistAnnotatedString(
                        context = context,
                        showData = state.showData.first(),
                        includeSoundcheck = true,
                        includeFooter = true
                    )

                    // Soundcheck & setlist
                    Text(
                        modifier = Modifier.padding(6.dp),
                        text = songsStr
                    )

                    // Footer
                    // Include 'footer' meanings if enabled & present: "[1] This is a footer."
                    if (state.showData.first().footnoteList.isNotEmpty()) {
                        Text(
                            modifier = Modifier.padding(start = 6.dp, end = 6.dp),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            text = "\n${context.resources.getString(R.string.setlist_footnotes)}"
                        )

                        for (j in state.showData.first().footnoteList.indices) {
                            val footerStr: AnnotatedString = buildAnnotatedString {

                                val footer = state.showData.first().footnoteList[j]
                                // Add footer number in bracket: "[1]"
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("[${j + 1}] ")
                                }
                                // Add footer text
                                append(footer)


                            }
                            Text(
                                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                                text = footerStr
                            )
                        }
                    }

                    // Setlist notes
                    BuildSetlistNotes(
                        LocalContext.current,
                        state.showData.first().setlistNotes.toString(),
                        true
                    )
                }
            }
        } else if (state.errorMessage?.isNotEmpty() == true || !state.isLoading) {
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
}
