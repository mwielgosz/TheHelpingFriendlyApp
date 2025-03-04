package com.infiniwaresolutions.thehelpingfriendlyapp.helpers

import android.util.Log
import com.infiniwaresolutions.thehelpingfriendlyapp.data.local.ShowData
import com.infiniwaresolutions.thehelpingfriendlyapp.data.network.DotNetSongData


// Organize & convert messy DotNet JSON Data to a List<ShowData> object
/**
 * Returns [List] of [ShowData] compiled from messy Phish.net API [List] of [DotNetSongData] data.
 * Pass [keepAllResults] to truncates the final result in [List] [DotNetSongData]
 */
internal fun organizeDataFromJson(
    dotNetDotNetSongDataList: List<DotNetSongData>?,
    keepAllResults: Boolean
): List<ShowData> {
    val sortedShowDataList: MutableList<ShowData> = mutableListOf()
    var lastShowId: Int?

    // List of unique DotNetSetlist.Data objects by showId (only pull one element of each show's data)
    val uniqueDotNetSetlistDataIdList = dotNetDotNetSongDataList?.distinctBy { it.showId }
        ?.toMutableList()

    if (!keepAllResults) {
        // Find & remove all last show data from List before processing as it likely will be incomplete
        uniqueDotNetSetlistDataIdList?.size?.let {
            if (it > 1) {
                uniqueDotNetSetlistDataIdList.removeAll { it.showId == uniqueDotNetSetlistDataIdList[uniqueDotNetSetlistDataIdList.lastIndex].showId }
            }
        }
    }

    Log.d("DotNetParser", "Unique DotNet Data items: ${uniqueDotNetSetlistDataIdList?.size}")

    if (uniqueDotNetSetlistDataIdList != null) {
        for (dotNetSetlistData in uniqueDotNetSetlistDataIdList) {
            val songList: MutableList<String> = mutableListOf()
            val songSetList: MutableList<String> = mutableListOf()
            val transMarkList: MutableList<String> = mutableListOf()
            val footnoteIndexList: MutableList<Int> = mutableListOf()
            val footnoteList: MutableList<String> = mutableListOf()

            lastShowId = dotNetSetlistData.showId

            // Filter by showId and sort each DotNetSetlist.Data object by uniqueId (orders setlist)
            val filteredDotNetSongDataList: List<DotNetSongData> =
                dotNetDotNetSongDataList.filter { it.showId == lastShowId }
            val sortedDotNetSetlistDataList =
                filteredDotNetSongDataList.sortedBy { it.uniqueId }.toMutableList()

            Log.d(
                "DotNetParser",
                "Sorted DotNet Data list size: ${sortedDotNetSetlistDataList.size}"
            )

            for (item in sortedDotNetSetlistDataList) {
                // Parse & add 'footnote' related object fields
                if (item.footnote != null && item.footnote != "" && item.footnote != item.songName) {
                    var footnoteIdx: Int
                    if (footnoteList.contains(item.footnote)) {
                        footnoteIdx = footnoteList.indexOf(item.footnote) + 1
                    } else {
                        footnoteIdx = footnoteList.size + 1
                        footnoteList.add(item.footnote)
                    }
                    if (footnoteIdx == 0) footnoteIdx = 1
                    footnoteIndexList.add(footnoteIdx)
                } else {
                    footnoteIndexList.add(0)
                }
                // Add song name & transition mark to object
                item.songName?.let { songList.add(it) }
                item.transMark?.let { transMarkList.add(it) }

                songSetList.add(item.set.toString())
            }

            // Format date from "yyyy-mm-dd" to "January 1, 2025"
            //val dotNetDate = LocalDate.parse(dotNetSetlistData.showDate)
            //val formattedDate = dotNetDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))

            // Create ShowData object & pass values
            val showToAdd = dotNetSetlistData.showId?.let {
                ShowData(
                    showId = it,
                    showDate = dotNetSetlistData.showDate,
                    permalink = dotNetSetlistData.permalink,
                    showYear = dotNetSetlistData.showYear,
                    reviews = dotNetSetlistData.reviews,
                    exclude = dotNetSetlistData.exclude,
                    soundcheck = dotNetSetlistData.soundcheck,
                    footnoteList = footnoteList,
                    footnoteIndexList = footnoteIndexList,
                    tourId = dotNetSetlistData.tourId,
                    tourName = dotNetSetlistData.tourName,
                    tourWhen = dotNetSetlistData.tourWhen,
                    venueId = dotNetSetlistData.venueId,
                    venue = dotNetSetlistData.venue,
                    city = dotNetSetlistData.city,
                    state = dotNetSetlistData.state,
                    country = dotNetSetlistData.country,
                    artistId = dotNetSetlistData.artistId,
                    artistSlug = dotNetSetlistData.artistSlug,
                    artistName = dotNetSetlistData.artistName,
                    setlistNotes = dotNetSetlistData.setlistNotes,
                    transMarkList = transMarkList,
                    songSetList = songSetList,
                    songList = songList
                )
            }

            showToAdd?.let { sortedShowDataList.add(it) }
        }
    }

    return sortedShowDataList
}

fun organizeDotNetSetlist(dotNetDotNetSongDataList: List<DotNetSongData>?): List<DotNetSongData> {
    // List of unique DotNetSetlist.Data objects by showId (only pull one element of each show's data)
    val uniqueDotNetSetlistDataIdList = dotNetDotNetSongDataList?.distinctBy { it.showId }
        ?.toMutableList()

    if (uniqueDotNetSetlistDataIdList != null && uniqueDotNetSetlistDataIdList.isNotEmpty()) {
        // Find & remove all last show data from List before processing as it likely will be incomplete
        uniqueDotNetSetlistDataIdList.removeAll { it.showId == uniqueDotNetSetlistDataIdList[uniqueDotNetSetlistDataIdList.lastIndex].showId }

        // X Loop through each showId
        // X get List of all elements with same showId
        // X ensure list is properly sorted by uniqueId (song order)
        // remove any duplicates (or combine) - ex. same song, different footnotes
        // add all to list for return

        Log.d("DotNetParser", "Unique DotNet Data items: ${uniqueDotNetSetlistDataIdList.size}")

        var sortedDotNetSetlistData: List<DotNetSongData>
        var lastShowId: Int?

        for (dotNetSetlistData in uniqueDotNetSetlistDataIdList) {
            lastShowId = dotNetSetlistData.showId

            // Filter by showId and sort each DotNetSetlist.Data object by uniqueId (orders setlist)
            val filteredDotNetSongDataList: List<DotNetSongData> =
                dotNetDotNetSongDataList.filter { it.showId == lastShowId }
                    .sortedBy { it.uniqueId }




            Log.d(
                "DotNetParser",
                "Sorted DotNet Data list size: ${filteredDotNetSongDataList.size}"
            )
        }

    }

    return uniqueDotNetSetlistDataIdList ?: emptyList()
}
