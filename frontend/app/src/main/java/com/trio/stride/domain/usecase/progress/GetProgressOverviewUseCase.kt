package com.trio.stride.domain.usecase.progress

import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.data.local.dao.AvailableSportDao
import com.trio.stride.data.local.entity.AvailableSportEntity
import com.trio.stride.data.local.entity.ProgressEntity
import com.trio.stride.data.remote.dto.ProgressListDto
import com.trio.stride.domain.repository.ProgressRepository
import com.trio.stride.ui.utils.parseErrorResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetProgressOverviewUseCase @Inject constructor(
    private val repository: ProgressRepository,
    private val availableSportDao: AvailableSportDao
) {
    operator fun invoke(): Flow<Resource<ProgressListDto?>> = flow {
        emit(Resource.Loading())
        try {
            val response = repository.getProgressOverview()
            if (response.isSuccessful) {
                val remoteData = response.body()

                if (remoteData != null) {
                    val sportsEntities = remoteData.data.map { sportWithProgressDto ->
                        AvailableSportEntity(
                            id = sportWithProgressDto.sport.id,
                            name = sportWithProgressDto.sport.name,
                            image = sportWithProgressDto.sport.image,
                            sportMapType = sportWithProgressDto.sport.sportMapType?.name
                        )
                    }
                    availableSportDao.upsertSports(sportsEntities)

                    val progressEntities = remoteData.data.flatMap { sportWithProgressDto ->
                        sportWithProgressDto.progresses.map { progressDto ->
                            ProgressEntity(
                                sportId = sportWithProgressDto.sport.id,
                                fromDate = progressDto.fromDate,
                                toDate = progressDto.toDate,
                                distance = progressDto.distance,
                                elevation = progressDto.elevation,
                                time = progressDto.time,
                                numberActivities = progressDto.numberActivities
                            )
                        }
                    }
                    repository.upsertProgressOverview(progressEntities)
                }
                emit(Resource.Success(remoteData))
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