package com.infiniwaresolutions.thehelpingfriendlyapp.helpers

import com.infiniwaresolutions.thehelpingfriendlyapp.data.DotNetSetlistSongData


/**
 * Returns [List] of [List] of [DotNetSetlistSongData] compiled from messy Phish.net API [List] of [DotNetSetlistSongData] data.
 * Pass [keepAllResults] to truncates the final result in [List] [DotNetSetlistSongData]
 */
fun organizeDotNetSetlist(
    dotNetDotNetSetlistSongDataList: List<DotNetSetlistSongData>?,
    keepAllResults: Boolean
): List<List<DotNetSetlistSongData>> {
    // List of unique DotNetSetlist.Data objects by showId (only pull one element of each show's data)
    val uniqueDotNetSetlistDataIdList = dotNetDotNetSetlistSongDataList?.distinctBy { it.showId }
        ?.toMutableList()

    if (!uniqueDotNetSetlistDataIdList.isNullOrEmpty()) {

        // Find & remove all last show data from List before processing as it likely will be incomplete
        if (!keepAllResults && uniqueDotNetSetlistDataIdList.size > 1) {
            uniqueDotNetSetlistDataIdList.removeAll { it.showId == uniqueDotNetSetlistDataIdList[uniqueDotNetSetlistDataIdList.lastIndex].showId }
        }

        //Log.d("DotNetParser", "Unique DotNet Data items: ${uniqueDotNetSetlistDataIdList.size}")

        val sortedDotNetSetlistData = mutableListOf<MutableList<DotNetSetlistSongData>>()
        var lastShowId: Int?

        for (dotNetSetlistData in uniqueDotNetSetlistDataIdList) {
            lastShowId = dotNetSetlistData.showId

            // Filter by showId and sort each DotNetSetlist.Data object by uniqueId (orders setlist)
            val filteredDotNetSetlistSongDataList: MutableList<DotNetSetlistSongData> =
                dotNetDotNetSetlistSongDataList.filter { it.showId == lastShowId }
                    .sortedBy { it.position }.toMutableList()

            filteredDotNetSetlistSongDataList.filter { it.footnote == it.songName }
                .forEach {
                    it.footnote = ""
                }

            sortedDotNetSetlistData.add(filteredDotNetSetlistSongDataList)

            /*Log.d(
                "DotNetParser",
                "Sorted DotNet Data list size: ${filteredDotNetSongDataList.size}"
            )*/
        }
        return sortedDotNetSetlistData
    }
    return emptyList()
}
