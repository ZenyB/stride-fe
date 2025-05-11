package com.trio.stride.data.datastoremanager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.trio.stride.base.Resource
import com.trio.stride.base.UnknownException
import com.trio.stride.domain.model.UserInfo
import com.trio.stride.domain.usecase.profile.GetUserUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val getUserUseCase: GetUserUseCase
) {

    companion object {
        public val USER_KEY = stringPreferencesKey("user_json")
    }

    suspend fun saveUser(user: UserInfo) {
        dataStore.edit { prefs ->
            prefs[USER_KEY] = Gson().toJson(user)
        }
    }

    fun getUser(): Flow<UserInfo?> = dataStore.data.map { prefs ->
        prefs[USER_KEY]?.let {
            Gson().fromJson(it, UserInfo::class.java)
        }
    }

    fun refreshUser(): Flow<Resource<UserInfo>> =
        getUserUseCase().onEach { response ->
            if (response is Resource.Success) {
                saveUser(response.data)
            }
        }.catch { e ->
            emit(Resource.Error(UnknownException("Unexpected error: ${e.message}")))
        }


    suspend fun clearUser() {
        dataStore.edit { prefs ->
            prefs.remove(USER_KEY)
        }
    }
}
