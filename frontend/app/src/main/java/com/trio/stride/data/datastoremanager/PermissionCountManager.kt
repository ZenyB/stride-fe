package com.trio.stride.data.datastoremanager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionCountManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val PUSH_NOTI_LAST_REQUEST_TIME = longPreferencesKey("push_noti_last_request_time")
        private val LOCATION_LAST_REQUEST_TIME = longPreferencesKey("location_last_request_time")
        private val PUSH_NOTIFICATION_PERMISSION_COUNT = intPreferencesKey("push_noti_count")
        private val LOCATION_PERMISSION_COUNT = intPreferencesKey("location_count")
        private val todayInLongMillis =
            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun getPushNotiCount(): Flow<Int?> {
        return dataStore.data.map { preps ->
            preps[PUSH_NOTIFICATION_PERMISSION_COUNT]
        }
    }

    fun getLocationCount(): Flow<Int?> {
        return dataStore.data.map { preps ->
            preps[LOCATION_PERMISSION_COUNT]
        }
    }

    suspend fun plusPushNotiCount() {
        dataStore.edit { preps ->
            val pushNotiCount = preps[PUSH_NOTIFICATION_PERMISSION_COUNT]?.plus(1) ?: 1
            preps[PUSH_NOTIFICATION_PERMISSION_COUNT] = pushNotiCount
            if (pushNotiCount >= 2) {
                preps[PUSH_NOTI_LAST_REQUEST_TIME] = todayInLongMillis
            }
        }
    }

    suspend fun plusLocationCount() {
        dataStore.edit { preps ->
            val locationCount = preps[LOCATION_PERMISSION_COUNT]?.plus(1) ?: 1
            preps[LOCATION_PERMISSION_COUNT] = locationCount
            if (locationCount >= 2) {
                preps[LOCATION_LAST_REQUEST_TIME] = todayInLongMillis
            }
        }
    }

    suspend fun resetPushNotiCount() {
        dataStore.edit { preps ->
            preps[PUSH_NOTIFICATION_PERMISSION_COUNT] = 0
        }
    }

    suspend fun resetLocationCount() {
        dataStore.edit { preps ->
            preps[LOCATION_PERMISSION_COUNT] = 0
        }
    }
}