package com.infiniwaresolutions.thehelpingfriendlyapp.data.remote

import com.infiniwaresolutions.thehelpingfriendlyapp.data.models.DotNetSetlistData
import com.infiniwaresolutions.thehelpingfriendlyapp.data.models.DotNetShowData
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindDotNetRepository(
        dotNetRepositoryImpl: DotNetRepositoryImpl
    ): DotNetRepository
}

interface DotNetRepository {
    suspend fun getAllDotNetShows(): Resource<DotNetShowData>
    suspend fun getAllDotNetSetlists(): Resource<DotNetSetlistData>
    suspend fun getDotNetSetlistsByLimit(@Query("limit") limit: Int): Resource<DotNetSetlistData>
    suspend fun getDotNetSetlistByShowId(@Query("showid") showId: Int?): Resource<DotNetSetlistData>
    suspend fun getDotNetSearchByShowDate(@Path("showDate") showDate: String?): Resource<DotNetSetlistData>
}
