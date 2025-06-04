package com.trio.stride.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.trio.stride.data.datastoremanager.FCMTokenManager
import com.trio.stride.domain.usecase.fcmnotification.SaveFCMTokenUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class SendFCMTokenWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val fcmTokenManager: FCMTokenManager,
    private val saveFCMTokenUseCase: SaveFCMTokenUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.i("SFCMTK_Worker", "Do work")
        val token = fcmTokenManager.getToken().firstOrNull()
        val isSynced = fcmTokenManager.isTokenSynced().firstOrNull() ?: false

        if (token.isNullOrEmpty() || isSynced) {
            return Result.success()
        }

        return try {
            saveFCMTokenUseCase(token)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}