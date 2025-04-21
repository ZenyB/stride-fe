package com.trio.stride.data.datastoremanager

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        public val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        public val EXPIRY_TIME_KEY = stringPreferencesKey("access_token_expiry")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAccessToken(): Flow<String?> = dataStore.data.map { prefs ->
        val token = prefs[ACCESS_TOKEN_KEY]
        val expiryTimeString = prefs[EXPIRY_TIME_KEY]

        var isValid = true
        expiryTimeString?.let {
            val expiryTime = ZonedDateTime.parse(it, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            isValid = !ZonedDateTime.now().isAfter(expiryTime)
        }

        if (isValid) token else {
            clearTokens()
            null
        }
    }

    suspend fun saveAccessToken(token: String, expiryTime: String) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = token
            prefs[EXPIRY_TIME_KEY] = expiryTime
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isAccessTokenExpired(): Flow<Boolean> = dataStore.data.map { prefs ->
        val expiryStr = prefs[stringPreferencesKey("access_token_expiry")]
        expiryStr?.let {
            val expiryTime = ZonedDateTime.parse(it, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            ZonedDateTime.now().isAfter(expiryTime)
        } != false
    }

    suspend fun clearTokens() {
        dataStore.edit { preps ->
            preps.remove(ACCESS_TOKEN_KEY)
            preps.remove(EXPIRY_TIME_KEY)
        }
    }
}
