package com.trio.stride.data.datastoremanager

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FCMTokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("fcm_token")
    }

    fun getToken(): Flow<String?> {
        return dataStore.data.map { preps ->
            preps[TOKEN_KEY]
        }
    }

    suspend fun setToken(token: String) {
        Log.i("SET_LOCAL_FCM_TOKEN", token)
        dataStore.edit { preps ->
            preps[TOKEN_KEY] = token
        }
    }

    suspend fun deleteToken() {
        dataStore.edit { preps ->
            preps.remove(TOKEN_KEY)
        }
    }
}