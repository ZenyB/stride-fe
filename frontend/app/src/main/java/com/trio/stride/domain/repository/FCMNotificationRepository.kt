package com.trio.stride.domain.repository

import kotlinx.coroutines.flow.Flow

interface FCMNotificationRepository {
    suspend fun saveToken(token: String): Boolean
    suspend fun getLocalToken(): Flow<String?>
    suspend fun deleteToken(token: String): Boolean
    suspend fun deleteLocalToken()
    suspend fun setIsTokenSynced(isSynced: Boolean)
    suspend fun isTokenSynced(): Flow<Boolean>
    suspend fun addTokenToDelete(token: String)
    suspend fun removeTokenToDelete(token: String)
}