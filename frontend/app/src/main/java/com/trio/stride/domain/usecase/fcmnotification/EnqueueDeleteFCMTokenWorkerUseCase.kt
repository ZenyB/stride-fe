package com.trio.stride.domain.usecase.fcmnotification

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.trio.stride.worker.DeleteFCMTokenWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class EnqueueDeleteFCMTokenWorkerUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    operator fun invoke() {
        Log.i("START_DEL_FCM_TK_WORKER", "started")

        val request = OneTimeWorkRequestBuilder<DeleteFCMTokenWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "delete_fcm_token",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}