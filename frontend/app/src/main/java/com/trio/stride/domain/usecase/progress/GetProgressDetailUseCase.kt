package com.trio.stride.domain.usecase.progress

import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.data.local.dao.AvailableSportDao
import com.trio.stride.data.local.entity.AvailableSportEntity
import com.trio.stride.domain.model.ProgressDetails
import com.trio.stride.domain.repository.ProgressRepository
import com.trio.stride.ui.utils.parseErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetProgressDetailUseCase @Inject constructor(
    private val repository: ProgressRepository,
    private val availableSportDao: AvailableSportDao
) {
    operator fun invoke(sportId: String): Flow<Resource<ProgressDetails?>> = flow {
        emit(Resource.Loading())
        try {
            val response = repository.getProgressDetail(sportId = sportId)
            if (response.isSuccessful) {
                val remoteData = response.body()

                if (remoteData != null) {
                    val sportsEntities = remoteData.availableSports.map { sportWithProgressDto ->
                        AvailableSportEntity(
                            id = sportWithProgressDto.id,
                            name = sportWithProgressDto.name,
                            image = sportWithProgressDto.image,
                            sportMapType = sportWithProgressDto.sportMapType?.name
                        )
                    }
                    availableSportDao.upsertSports(sportsEntities)
                }
                emit(Resource.Success(remoteData?.progresses))
                return@flow
            } else {
                val errorResponse = parseErrorResponse(response.errorBody())
                emit(
                    Resource.Error(
                        error = UnknownException(errorResponse.message),
                    )
                )
            }
        } catch (e: Exception) {
            emit(Resource.Error(UnknownException(e.message.toString())))
        }
    }
}