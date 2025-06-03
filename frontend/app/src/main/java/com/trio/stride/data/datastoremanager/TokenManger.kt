package com.trio.stride.data.datastoremanager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        public val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        public val EXPIRY_TIME_KEY = longPreferencesKey("access_token_expiry")
    }

    fun getAccessToken(): Flow<String?> = dataStore.data.map { prefs ->
        val token = prefs[ACCESS_TOKEN_KEY]
        val expiryTime = prefs[EXPIRY_TIME_KEY]

        var isValid = true
        expiryTime?.let {
            val expiryTimeDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
            isValid = !ZonedDateTime.now().isAfter(expiryTimeDate)
        }

        if (isValid) token else {
            clearTokens()
            null
        }
    }

    suspend fun saveAccessToken(token: String, expiryTime: Long) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = token
            prefs[EXPIRY_TIME_KEY] = expiryTime
        }
    }

    suspend fun clearTokens() {
        dataStore.edit { preps ->
            preps.remove(ACCESS_TOKEN_KEY)
            preps.remove(EXPIRY_TIME_KEY)
        }
    }
}
