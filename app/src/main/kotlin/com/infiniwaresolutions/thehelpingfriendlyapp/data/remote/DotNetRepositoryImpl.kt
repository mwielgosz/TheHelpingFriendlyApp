package com.infiniwaresolutions.thehelpingfriendlyapp.data.remote

import com.infiniwaresolutions.thehelpingfriendlyapp.data.models.DotNetSetlistData
import com.infiniwaresolutions.thehelpingfriendlyapp.data.models.DotNetShowData
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

class DotNetRepositoryImpl @Inject constructor(
    private val dotNetApiService: DotNetApiService
) : DotNetRepository {
    override suspend fun getAllDotNetShows(): Resource<DotNetShowData> =
        safeApiCall { dotNetApiService.getAllDotNetShows() }

    override suspend fun getAllDotNetSetlists(): Resource<DotNetSetlistData> =
        safeApiCall { dotNetApiService.getAllDotNetSetlists() }

    override suspend fun getDotNetSetlistsByLimit(limit: Int): Resource<DotNetSetlistData> =
        safeApiCall { dotNetApiService.getDotNetSetlistsByLimit(limit = limit) }

    override suspend fun getDotNetSetlistByShowId(showId: Int?): Resource<DotNetSetlistData> =
        safeApiCall { dotNetApiService.getDotNetSetlistByShowId(showId = showId) }

    override suspend fun getDotNetSearchByShowDate(showDate: String?): Resource<DotNetSetlistData> =
        safeApiCall { dotNetApiService.getDotNetSearchByShowDate(showDate = showDate) }

    // Safe API call handler with suspend
    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Resource<T> {
        return try {
            val response = apiCall()
            when {
                response.isSuccessful -> {
                    response.body()?.let { Resource.Success(it) }
                        ?: Resource.Error("HTTP 200: Empty response body")
                }

                else -> Resource.Error("HTTP Error: ${response.code()} - ${response.message()}")
            }
        } catch (exception: Throwable) {
            handleApiError(exception)
        }
    }

    // Exception handling with detailed Resource.Error
    private fun <T> handleApiError(exception: Throwable): Resource<T> {
        val message = when (exception) {
            is TimeoutException -> "Request timed out. Please try again."
            is IOException -> "Network error. Please check your connection."
            is HttpException -> {
                when (val statusCode = exception.code()) {
                    400 -> "Bad Request"
                    401 -> "Unauthorized. Please check your credentials."
                    403 -> "Forbidden. Access is denied."
                    404 -> "Resource not found."
                    500 -> "Internal Server Error. Please try again later."
                    503 -> "Service Unavailable. Please try again later."
                    else -> "Unexpected HTTP Error: $statusCode"
                }
            }

            is IllegalArgumentException -> "Invalid argument provided. ${exception.message}"
            is IllegalStateException -> "Illegal application state. ${exception.message}"
            else -> "Unexpected error occurred: ${exception.message}"
        }
        return Resource.Error(message, exception)
    }
}

// Resource wrapper for API call states
sealed class Resource<out T> {
    // Represents a loading state
    data object Loading : Resource<Nothing>()

    // Represents a successful result with data
    data class Success<T>(val data: T) : Resource<T>()

    // Represents an error state with a message and optional exception
    data class Error(
        val message: String,
        val exception: Throwable? = null
    ) : Resource<Nothing>()
}
