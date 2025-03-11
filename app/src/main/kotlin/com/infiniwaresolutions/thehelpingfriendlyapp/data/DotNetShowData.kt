package com.infiniwaresolutions.thehelpingfriendlyapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class DotNetShowData(
    @SerialName("error") var error: Boolean? = null,
    @SerialName("error_message") var errorMessage: String? = null,
    @SerialName("data") var dotNetShowData: List<DotNetShow> = arrayListOf()
)

@Serializable
data class DotNetShow(
    @SerialName("showid") var showId: Int? = null,
    @SerialName("showyear") var showYear: String? = null,
    @SerialName("showmonth") var showMonth: Int? = null,
    @SerialName("showday") var showDay: Int? = null,
    @SerialName("showdate") var showDate: String? = null,
    @SerialName("permalink") var permalink: String? = null,
    @SerialName("exclude_from_stats") var excludeFromStats: Int? = null,
    @SerialName("venueid") var venueId: Int? = null,
    @SerialName("setlist_notes") var setlistNotes: String? = null,
    @SerialName("venue") var venue: String? = null,
    @SerialName("city") var city: String? = null,
    @SerialName("state") var state: String? = null,
    @SerialName("country") var country: String? = null,
    @SerialName("artistid") var artistId: Int? = null,
    @SerialName("artist_name") var artistName: String? = null,
    @SerialName("tourid") var tourId: Int? = null,
    @SerialName("tour_name") var tourName: String? = null,
    @SerialName("created_at") var createdAt: String? = null,
    @SerialName("updated_at") var updatedAt: String? = null
)
