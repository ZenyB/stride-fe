package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.datastoremanager.FCMTokenManager
import com.trio.stride.data.remote.apiservice.fcmnotification.FCMNotificationApi
import com.trio.stride.domain.repository.FCMNotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FCMNotificationRepositoryImpl @Inject constructor(
    private val fcmNotificationApi: FCMNotificationApi,
    private val fcmTokenManager: FCMTokenManager
) : FCMNotificationRepository {
    override suspend fun saveToken(token: String): Boolean {
        return fcmNotificationApi.saveToken(token).data
    }

    override suspend fun deleteToken(token: String): Boolean {
        return fcmNotificationApi.deleteToken(token).data
    }

    override suspend fun getLocalToken(): Flow<String?> {
        return fcmTokenManager.getToken()
    }

    override suspend fun deleteLocalToken() {
        fcmTokenManager.deleteToken()
    }

    override suspend fun setIsTokenSynced(isSynced: Boolean) {
        fcmTokenManager.setIsTokenSynced(isSynced)
    }

    override suspend fun isTokenSynced(): Flow<Boolean> {
        return fcmTokenManager.isTokenSynced()
    }

    override suspend fun addTokenToDelete(token: String) {
        fcmTokenManager.addTokenToDelete(token)
    }

    override suspend fun removeTokenToDelete(token: String) {
        fcmTokenManager.removeTokenToDelete(token)
    }
}