package com.trio.stride.domain.usecase.fcmnotification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.trio.stride.data.datastoremanager.FCMTokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RefreshAndSaveFCMTokenUseCase @Inject constructor(
    private val fcmTokenManager: FCMTokenManager,
    private val saveFCMTokenUseCase: SaveFCMTokenUseCase,
    private val deleteFCMTokenUseCase: DeleteFCMTokenUseCase
) {
    suspend operator fun invoke() {
        withContext(Dispatchers.IO) {
            val newToken = FirebaseMessaging.getInstance().token.await()
            val oldToken = fcmTokenManager.getToken().firstOrNull()
            if (!oldToken.isNullOrBlank() && oldToken != newToken) {
                deleteFCMTokenUseCase(oldToken)
            }
            saveFCMTokenUseCase(newToken).lastOrNull()
            fcmTokenManager.setToken(newToken)
            Log.i("SEND_FCM_TOKEN_TO_SERVER_AND_SAVE", newToken)
        }
    }
}