package com.trio.stride.data.datastoremanager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import javax.inject.Inject

class UserManager @Inject constructor(val dataStore: DataStore<Preferences>) {
//
//    companion object {
//        val USER_ID = stringPreferencesKey("user_id")
//        val USER_NAME = stringPreferencesKey("user_name")
//        val USER_AVA = stringPreferencesKey("user_ava")
//        // ... Thêm key cho các field khác nếu cần
//    }
//
//    suspend fun saveUser(user: UserInfo) {
//        dataStore.edit { prefs ->
//            prefs[USER_ID] = user.id
//            prefs[USER_NAME] = user.name
//            prefs[USER_AVA] = user.ava
//            // ... Lưu các field khác
//        }
//    }
//
//    fun getUser(): Flow<UserInfo> = dataStore.data.map { prefs ->
//        UserInfo(
//            id = prefs[USER_ID] ?: "",
//            name = prefs[USER_NAME] ?: "",
//            ava = prefs[USER_AVA] ?: "",
//            // ... Gán các field khác
//        )
//    }
}
