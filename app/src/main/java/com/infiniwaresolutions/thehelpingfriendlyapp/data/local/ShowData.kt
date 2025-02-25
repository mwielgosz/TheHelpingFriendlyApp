package com.infiniwaresolutions.thehelpingfriendlyapp.data.local

import kotlinx.serialization.Serializable


// DATA CLASS TO STORE SETLIST DATA THAT IS MORE COMPATIBLE
// THIS IS A **TEMPORARY SOLUTION**
// WE WANT TO ONLY USE THE DotNetSetlistData DATA CLASS
@Serializable
data class ShowData(
    var showId: Int,
    var showDate: String? = null,
    var permalink: String? = null,
    var showYear: String? = null,
    var reviews: Int? = null,
    var exclude: Int? = null,
    var soundcheck: String? = null,
    var footnoteList: List<String> = listOf(),
    var footnoteIndexList: List<Int> = listOf(),
    var tourId: Int? = null,
    var tourName: String? = null,
    var tourWhen: String? = null,
    var venueId: Int? = null,
    var venue: String? = null,
    var city: String? = null,
    var state: String? = null,
    var country: String? = null,
    var artistId: Int? = null,
    var artistSlug: String? = null,
    var artistName: String? = null,
    var setlistNotes: String? = null,
    var transMarkList: List<String> = listOf(),
    var songSetList: List<String> = listOf(),
    var songList: List<String> = listOf()
)

data class SongData(
    var name: String,
    var set: String,
    var transMark: String
)
