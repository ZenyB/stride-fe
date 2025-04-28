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
class MapStyleManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        public val MAP_STYLE_KEY = stringPreferencesKey("map_style")
    }

    fun getMapStyle(): Flow<String?> = dataStore.data.map { prefs ->
        Log.d("mapStyle", prefs.toString())
        prefs[MAP_STYLE_KEY]
    }

    suspend fun saveMapStyle(style: String) {
        dataStore.edit { prefs ->
            prefs[MAP_STYLE_KEY] = style
        }
    }
}