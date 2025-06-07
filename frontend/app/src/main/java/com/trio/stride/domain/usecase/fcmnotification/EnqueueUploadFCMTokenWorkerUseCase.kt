package com.trio.stride.domain.usecase.fcmnotification

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.trio.stride.data.local.dao.CurrentUserDao
import com.trio.stride.worker.SendFCMTokenWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import javax.inject.Inject

class EnqueueUploadFCMTokenWorkerUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDao: CurrentUserDao,
) {
    suspend operator fun invoke() {
        if (userDao.getCurrentUser() == null) return
        Log.i("START_WORKER", "started")

        val request = OneTimeWorkRequestBuilder<SendFCMTokenWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.LINEAR,
                duration = Duration.ofSeconds(10)
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "upload_fcm_token",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
