package com.infiniwaresolutions.thehelpingfriendlyapp.data.network


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val PHISH_NET_API_KEY: String = "2897CFB45FE6C2A02460"

interface DotNetApiService {
    // Get first 200 shows from Shows endpoint
    @GET("shows.json")
    suspend fun getAllDotNetShows(
        @Query("order_by") orderBy: String = "showdate",
        @Query("limit") limit: String = "200",
        @Query("direction") direction: String = "desc",
        @Query("apikey") apikey: String = PHISH_NET_API_KEY
    ): Response<DotNetShowData>

    // Get first 200 songs from Setlists endpoint
    @GET("setlists.json")
    suspend fun getAllDotNetSetlists(
        @Query("order_by") orderBy: String = "showdate",
        @Query("limit") limit: String = "200",
        @Query("direction") direction: String = "desc",
        @Query("apikey") apikey: String = PHISH_NET_API_KEY
    ): Response<DotNetSetlistData>

    // Get specific number of songs from Setlists endpoint
    @GET("setlists.json")
    suspend fun getDotNetSetlistsByLimit(
        @Query("order_by") orderBy: String = "showdate",
        @Query("limit") limit: Int,
        @Query("direction") direction: String = "desc",
        @Query("apikey") apikey: String = PHISH_NET_API_KEY
    ): Response<DotNetSetlistData>

    // Get songs by show ID from Setlists endpoint
    @GET("setlists/showid/{showId}.json")
    suspend fun getDotNetSetlistByShowId(
        @Path("showId") showId: Int?,
        //@Query("direction") direction: String = "desc",
        @Query("apikey") apikey: String = PHISH_NET_API_KEY
    ): Response<DotNetSetlistData>

    // Get songs by show date from Setlists endpoint - showDate in yyyy-mm-dd format
    @GET("setlists/showdate/{showDate}.json")
    suspend fun getDotNetSearchByShowDate(
        @Path("showDate") showDate: String,
        @Query("direction") direction: String = "desc",
        @Query("apikey") apikey: String = PHISH_NET_API_KEY
    ): Response<DotNetSetlistData>
}
