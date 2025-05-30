package com.trio.stride.domain.usecase.sport

import android.util.Log
import com.trio.stride.base.NetworkException
import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.repository.SportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class GetSportsUseCase(
    private val sportRepository: SportRepository
) {

    operator fun invoke(
        name: String? = null,
        categoryId: String? = null,
        forceRefresh: Boolean = false,
    ): Flow<Resource<List<Sport>>> = flow {
        emit(Resource.Loading())

        val localData = sportRepository.getLocalSports(categoryId)
        Log.i("SPORT_LOCAL", localData.toString())
        if (localData.isNotEmpty() && !forceRefresh) {
            Log.i("SPORT_LOCAL", localData.toString())
            emit(Resource.Success(localData))
            return@flow
        }

        try {
            val remoteData = sportRepository.getSports(name, categoryId)

            sportRepository.insertSports(remoteData)

            val updatedLocal = sportRepository.getLocalSports(categoryId)
            Log.i("SPORT_REMOTE", updatedLocal.toString())
            emit(Resource.Success(updatedLocal))
        } catch (e: IOException) {
            emit(Resource.Error(NetworkException(e.message.toString())))
            Log.i("SPORT_ERROR", e.message.toString())
        } catch (e: Exception) {
            emit(Resource.Error(UnknownException(e.message.toString())))
            Log.i("SPORT_ERROR", e.message.toString())
        }
    }

}