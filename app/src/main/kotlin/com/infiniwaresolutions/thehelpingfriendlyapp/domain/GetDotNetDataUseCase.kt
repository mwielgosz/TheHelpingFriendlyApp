package com.infiniwaresolutions.thehelpingfriendlyapp.domain

import com.infiniwaresolutions.thehelpingfriendlyapp.data.network.DotNetRepository
import com.infiniwaresolutions.thehelpingfriendlyapp.data.network.DotNetSetlistData
import com.infiniwaresolutions.thehelpingfriendlyapp.data.network.DotNetShowData
import com.infiniwaresolutions.thehelpingfriendlyapp.data.network.Resource
import javax.inject.Inject

class GetAllDotNetShowsUseCase @Inject constructor(private val repository: DotNetRepository) {
    suspend operator fun invoke(): Resource<DotNetShowData> = repository.getAllDotNetShows()
}

class GetAllDotNetSetlistsUseCase @Inject constructor(private val repository: DotNetRepository) {
    suspend operator fun invoke(): Resource<DotNetSetlistData> = repository.getAllDotNetSetlists()
}

class GetAllDotNetSetlistsByLimitUseCase @Inject constructor(private val repository: DotNetRepository) {
    suspend operator fun invoke(limit: Int): Resource<DotNetSetlistData> =
        repository.getDotNetSetlistsByLimit(limit)
}

class GetDotNetSetlistByShowIdUseCase @Inject constructor(private val repository: DotNetRepository) {
    suspend operator fun invoke(showId: Int?): Resource<DotNetSetlistData> =
        repository.getDotNetSetlistByShowId(showId)
}

class GetDotNetSearchByShowDateUseCase @Inject constructor(private val repository: DotNetRepository) {
    suspend operator fun invoke(showDate: String?): Resource<DotNetSetlistData> =
        repository.getDotNetSearchByShowDate(showDate)
}
