package com.trio.stride.data.datastoremanager

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FCMTokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("fcm_token")
        private val IS_TOKEN_SYNCED = booleanPreferencesKey("is_token_synced")
        private val TOKENS_TO_DELETE_KEY = stringPreferencesKey("fcm_tokens_to_delete")
        private val IS_PENDING_DELETE = booleanPreferencesKey("is_pending_delete")
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

    fun isTokenSynced(): Flow<Boolean> {
        return dataStore.data.map { preps ->
            preps[IS_TOKEN_SYNCED] ?: false
        }
    }

    suspend fun setIsTokenSynced(isSynced: Boolean) {
        dataStore.edit { preps ->
            preps[IS_TOKEN_SYNCED] = isSynced
            preps[IS_PENDING_DELETE] = true
        }
    }

    suspend fun deleteToken() {
        dataStore.edit { preps ->
            preps.remove(TOKEN_KEY)
        }
    }

    suspend fun getTokensToDelete(): List<String> {
        val jsonString = dataStore.data.first()[TOKENS_TO_DELETE_KEY] ?: "[]"
        return Json.decodeFromString(jsonString)
    }

    suspend fun addTokenToDelete(token: String) {
        val tokens = getTokensToDelete().toMutableList()
        if (!tokens.contains(token)) {
            tokens.add(token)
            saveTokensToDelete(tokens)
        }
    }

    suspend fun removeTokenToDelete(token: String) {
        val tokens = getTokensToDelete().toMutableList()
        if (tokens.remove(token)) {
            saveTokensToDelete(tokens)
        }
    }

    private suspend fun saveTokensToDelete(tokens: List<String>) {
        dataStore.edit { prefs ->
            if (tokens.isEmpty()) {
                prefs.remove(TOKENS_TO_DELETE_KEY)
                prefs[IS_PENDING_DELETE] = false
            } else {
                prefs[TOKENS_TO_DELETE_KEY] =
                    Json.encodeToString(ListSerializer(String.serializer()), tokens)
                prefs[IS_PENDING_DELETE] = true
            }
        }
    }

    fun isPendingDelete(): Flow<Boolean> {
        return dataStore.data.map { preps ->
            preps[IS_PENDING_DELETE] ?: false
        }
    }

//    suspend fun setIsPendingDelete(isPendingDelete: Boolean) {
//        dataStore.edit { preps ->
//            preps[IS_PENDING_DELETE] = isPendingDelete
//        }
//    }

//    suspend fun deleteTokenToDelete() {
//        dataStore.edit { preps ->
//            preps.remove(TOKEN_TO_DELETE_KEY)
//        }
//    }
}