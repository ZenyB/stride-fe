package com.trio.stride.data.datastoremanager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MetadataManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        public val LAST_SPORT_FETCH_TIME = longPreferencesKey("last_sport_fetch_time")
    }

    fun getLastSportFetchTime(): Flow<Long?> = dataStore.data.map { prefs ->
        prefs[LAST_SPORT_FETCH_TIME]
    }

    suspend fun saveLastSportFetchTime() {
        dataStore.edit { prefs ->
            prefs[LAST_SPORT_FETCH_TIME] = System.currentTimeMillis()
        }
    }
}