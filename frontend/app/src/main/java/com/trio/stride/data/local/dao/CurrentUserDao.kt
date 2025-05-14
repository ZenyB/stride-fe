package com.trio.stride.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.trio.stride.data.local.entity.CurrentUserEntity

@Dao
interface CurrentUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCurrentUser(user: CurrentUserEntity)

    @Transaction
    @Query("SELECT * FROM current_user LIMIT 1")
    suspend fun getCurrentUser(): CurrentUserEntity?

    @Query("DELETE FROM current_user")
    suspend fun deleteCurrentUser()
}