package com.infiniwaresolutions.thehelpingfriendlyapp.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import com.infiniwaresolutions.thehelpingfriendlyapp.R
import com.infiniwaresolutions.thehelpingfriendlyapp.data.DotNetSetlistSongData
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * Returns a [CircularProgressIndicator] visual for loading data.
 */
@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
    ) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

/**
 * Returns a [Composable] that displays [errorStr] text.
 */
@Composable
fun NoDataErrorText(errorStr: String) {
    Text(
        text = errorStr,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        textAlign = TextAlign.Center
    )
}

/**
 * Returns a [Composable] for a vertical ScrollView with Pull to Refresh capabilities.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    contentAlignment: Alignment = Alignment.TopStart,
    indicator: @Composable BoxScope.() -> Unit = {
        Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = isRefreshing,
            state = state
        )
    },
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier.pullToRefresh(state = state, isRefreshing = isRefreshing, onRefresh = onRefresh),
        contentAlignment = contentAlignment
    ) {
        content()
        indicator()
    }
}

/**
 * Returns a [Composable] for [setlistNotes] with optional [showHeader]
 */
@Composable
fun BuildSetlistNotes(context: Context, setlistNotes: String, showHeader: Boolean) {
    if (setlistNotes != "") {
        var notesAlignment: TextAlign = TextAlign.Start

        // Setlist Notes Header
        if (showHeader) {
            Text(
                modifier = Modifier.padding(start = 6.dp, end = 6.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                text = "\n${context.resources.getString(R.string.setlist_notes)}"
            )
        } else notesAlignment = TextAlign.Center

        // Setlist notes
        Text(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            textAlign = notesAlignment,
            text = HtmlCompat.fromHtml(
                setlistNotes,
                HtmlCompat.FROM_HTML_MODE_COMPACT
            ).toString().trim()
        )
    }
}

/**
 * Builds an [AnnotatedString] based on [DotNetSetlistSongData] for setlist display.
 * Can disable soundcheck with [includeSoundcheck] and footer with [includeFootnotes]
 * Returns [Pair] of [AnnotatedString] [AnnotatedString] for rich display
 */
internal fun buildSetlistAndFooterAnnotatedString(
    dotNetData: List<DotNetSetlistSongData>,
    includeSoundcheck: Boolean,
    includeFootnotes: Boolean,
): Pair<AnnotatedString, AnnotatedString> {
    val footnoteAnnotatedStringBuilder = AnnotatedString.Builder("")
    val footnoteList = mutableListOf<String>()

    val setlistAnnotatedString: AnnotatedString = buildAnnotatedString {
        var lastSet = "1"

        // Include 'soundcheck' header & text if enabled & present: "Soundcheck: song1, song2"
        if (includeSoundcheck && dotNetData.first().soundcheck != "") {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Soundcheck:")
            }
            append(" ${dotNetData.first().soundcheck}\n")
        }

        // First set text: "Set 1: " - BOLD
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("Set 1: ")
        }

        for (song in dotNetData) {
            // Add new set name: "Encore: " - BOLD
            if (lastSet != song.set) {
                append("\n")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    if (song.set == "e") {
                        append("\nEncore: ")
                    } else if (song.set != "1") {
                        append("\nSet ${song.set}: ")
                    }
                }
            }

            // Add song name
            append(song.songName)

            // Include 'footer' markings if enabled & present: "[1]" - BOLD & string to builder
            if (includeFootnotes) {
                if (song.footnote != "") {
                    if (!footnoteList.contains(song.footnote)) {
                        // Add if not already in list
                        song.footnote?.let { footnoteList.add(it) }
                        val footerIdx = footnoteList.indexOf(song.footnote) + 1

                        // Append footnote number
                        footnoteAnnotatedStringBuilder.withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            footnoteAnnotatedStringBuilder.append("[${footerIdx}] ")
                        }
                        // Append footnote string
                        footnoteAnnotatedStringBuilder.append(song.footnote)
                        if (song.uniqueId != dotNetData.last().uniqueId) {
                            footnoteAnnotatedStringBuilder.appendLine()
                        }
                    }
                    // Get and add footnote number to setlist string
                    val footerIdx = footnoteList.indexOf(song.footnote) + 1
                    if (footerIdx != 0) {
                        withStyle(style = SpanStyle(baselineShift = BaselineShift.Superscript)) {
                            append("[$footerIdx]")
                        }
                    }
                }
            }

            // Add 'Transition' mark: " > "
            append(song.transMark)
            lastSet = song.set.toString()

        }
    }

    return Pair(setlistAnnotatedString, footnoteAnnotatedStringBuilder.toAnnotatedString())
}

/**
 * Returns formatted String of given [dateStr] in 'yyyy-MM-dd format or null if cannot format.
 */
fun isDateValidAndFormat(dateStr: String): String? {
    val formats = arrayOf(
        "yyyy-MM-dd",
        "yyyy/MM/dd",
        "yyyy.MM.dd",
        "MM-dd-yyyy",
        "MM/dd/yyyy",
        "MM.dd.yyyy",
        "yyyyMMdd",
        "MMddyyyy",
        "yyyyMMdd",
        "M-d-yyyy",
        "M/d/yyyy",
        "M.d.yyyy",
        "M-d-yy",
        "M/d/yy",
        "M.d.yy",
        "yyyy-M-d",
        "yyyy/M/d",
        "yyyy.M.d"
    )

    for (format in formats) {
        try {
            val dateFormatter = DateTimeFormatter.ofPattern(format)
            val date = LocalDate.parse(dateStr, dateFormatter)
            return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } catch (e: Exception) {
            // Continue to the next format if parsing fails
        }
    }
    return null // Return null if no format matches
}
