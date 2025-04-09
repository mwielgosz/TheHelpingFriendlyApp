package com.infiniwaresolutions.thehelpingfriendlyapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DotNetSetlistData(
    @SerialName(value = "error") val error: Boolean? = null,
    @SerialName(value = "error_message") val errorMessage: String? = null,
    @SerialName(value = "data") val dotNetSongEntities: List<DotNetSetlistSongData> = listOf()
)

@Serializable
data class DotNetSetlistSongData(
    @SerialName(value = "showid") val showId: Int? = null,
    @SerialName(value = "showdate") val showDate: String? = null,
    @SerialName(value = "permalink") val permalink: String? = null,
    @SerialName(value = "showyear") val showYear: String? = null,
    @SerialName(value = "uniqueid") val uniqueId: Int? = null,
    @SerialName(value = "meta") val meta: String? = null,
    @SerialName(value = "reviews") val reviews: Int? = null,
    @SerialName(value = "exclude") val exclude: Int? = null,
    @SerialName(value = "setlistnotes") val setlistNotes: String? = null,
    @SerialName(value = "soundcheck") val soundcheck: String? = null,
    @SerialName(value = "songid") val songId: Int? = null,
    @SerialName(value = "position") val position: Int? = null,
    @SerialName(value = "transition") val transition: Int? = null,
    @SerialName(value = "footnote") var footnote: String? = null,
    @SerialName(value = "set") val set: String? = null,
    @SerialName(value = "isjam") val isJam: Int? = null,
    @SerialName(value = "isreprise") val isReprise: Int? = null,
    @SerialName(value = "isjamchart") val isJamchart: Int? = null,
    @SerialName(value = "jamchart_description") val jamchartDescription: String? = null,
    @SerialName(value = "tracktime") val trackTime: String? = null,
    @SerialName(value = "gap") val gap: Int? = null,
    @SerialName(value = "tourid") val tourId: Int? = null,
    @SerialName(value = "tourname") val tourName: String? = null,
    @SerialName(value = "tourwhen") val tourWhen: String? = null,
    @SerialName(value = "song") val songName: String? = null,
    @SerialName(value = "nickname") val nickname: String? = null,
    @SerialName(value = "slug") val slug: String? = null,
    @SerialName(value = "is_original") val isOriginal: Int? = null,
    @SerialName(value = "venueid") val venueId: Int? = null,
    @SerialName(value = "venue") val venue: String? = null,
    @SerialName(value = "city") val city: String? = null,
    @SerialName(value = "state") val state: String? = null,
    @SerialName(value = "country") val country: String? = null,
    @SerialName(value = "trans_mark") val transMark: String? = null,
    @SerialName(value = "artistid") val artistId: Int? = null,
    @SerialName(value = "artist_slug") val artistSlug: String? = null,
    @SerialName(value = "artist_name") val artistName: String? = null
)
