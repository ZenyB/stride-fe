package com.trio.stride.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.trio.stride.data.datastoremanager.FCMTokenManager
import com.trio.stride.domain.usecase.fcmnotification.DeleteFCMTokenUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class DeleteFCMTokenWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val fcmTokenManager: FCMTokenManager,
    private val deleteFCMTokenUseCase: DeleteFCMTokenUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.i("DELFCMTK_Worker", "Do work")
        val tokens = fcmTokenManager.getTokensToDelete()
        val pendingDelete = fcmTokenManager.isPendingDelete().firstOrNull() ?: false

        if (tokens.isEmpty() || !pendingDelete) {
            return Result.success()
        }

        return try {
            tokens.forEach { token ->
                deleteFCMTokenUseCase(token)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}