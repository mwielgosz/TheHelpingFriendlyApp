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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
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
 * Builds a [Composable] based on [DotNetSetlistSongData] for soundcheck, setlist, and footer display.
 * Can disable soundcheck with [includeSoundcheck] and footer with [includeFootnotes]
 */
@Composable
fun BuildSetlistAndFooterAnnotatedString(
    context: Context,
    dotNetData: List<DotNetSetlistSongData>,
    includeSoundcheck: Boolean,
    includeFootnotes: Boolean,
) {
    val footnoteAnnotatedStringBuilder = AnnotatedString.Builder("")
    val footnoteList = mutableListOf<String>()

    val setlistAnnotatedString: AnnotatedString = buildAnnotatedString {
        var lastSet = "1"

        // Include 'soundcheck' header & text if enabled & present: "Soundcheck: song1, song2"
        if (includeSoundcheck && dotNetData.first().soundcheck != "") {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(context.resources.getString(R.string.setlist_soundcheck))
            }
            append(" ${dotNetData.first().soundcheck}\n")
        }

        // First set text: "Set 1: " - BOLD
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) {
            append("${context.resources.getString(R.string.setlist_set_1)} ")
        }

        for (song in dotNetData) {
            // Add new set name: "Encore: " - BOLD
            if (lastSet != song.set) {
                appendLine()
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) {
                    if (song.set == "e") {
                        append("\n${context.resources.getString(R.string.setlist_encore)} ")
                    } else if (song.set != "1") {
                        append("\n${context.resources.getString(R.string.setlist_set)} ${song.set}: ")
                    }
                }
            }

            // Add song name
            append(song.songName)

            // Include 'footer' markings if enabled & present: "[1]" - BOLD & string to builder
            if (includeFootnotes) {
                val footnote: String = song.footnote.toString()
                if (footnote != "") {
                    if (!footnoteList.contains(footnote)) {
                        // Add footnote if not already in list
                        footnoteList.add(footnote)
                        val footerIdx = footnoteList.indexOf(footnote) + 1

                        // Append footnote number - BOLD
                        footnoteAnnotatedStringBuilder.withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            // Put newline before next footnote if more than one
                            if (footnoteList.size > 1) {
                                footnoteAnnotatedStringBuilder.append("\n[${footerIdx}] ")
                            } else {
                                footnoteAnnotatedStringBuilder.append("[${footerIdx}] ")
                            }
                        }

                        // Append footnote string
                        footnoteAnnotatedStringBuilder.append(footnote)
                    }

                    // Get and add footnote number to setlist string
                    val footerIdx = footnoteList.indexOf(footnote) + 1
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

    Text(
        modifier = Modifier.padding(start = 6.dp, top = 24.dp, end = 6.dp),
        text = setlistAnnotatedString
    )

    if (includeFootnotes && footnoteList.size > 0) {
        Text(
            modifier = Modifier.padding(start = 6.dp, top = 24.dp, end = 6.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            text = context.resources.getString(R.string.setlist_footnotes)
        )

        Text(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            text = footnoteAnnotatedStringBuilder.toAnnotatedString()
        )
    }
}

/**
 * Builds a [Composable] for [setlistNotes] with optional [showHeader]
 */
@Composable
fun BuildSetlistNotes(context: Context, setlistNotes: String, showHeader: Boolean) {
    if (setlistNotes != "") {
        var notesAlignment: TextAlign = TextAlign.Start

        // Setlist Notes Header
        if (showHeader) {
            Text(
                modifier = Modifier.padding(start = 6.dp, top = 24.dp, end = 6.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                text = context.resources.getString(R.string.setlist_notes)
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
 * Builds a [Composable] with a clickable URL for phish.net website
 */
@Composable
fun BuildPhishNetClickableUrlText(context: Context, url: String) {
    val annotatedString = buildAnnotatedString {
        pushStringAnnotation(tag = url, annotation = url)
        withLink(
            LinkAnnotation.Url(
                url = url,
                styles = TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary))
            )
        ) { append(url) }
    }

    Text(
        modifier = Modifier.padding(start = 6.dp, top = 24.dp, end = 6.dp),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        text = context.resources.getString(R.string.read_on_phish_net)
    )

    Text(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
        text = annotatedString
    )
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
