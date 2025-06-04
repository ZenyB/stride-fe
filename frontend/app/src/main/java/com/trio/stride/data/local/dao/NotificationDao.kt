package com.trio.stride.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trio.stride.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY time DESC")
    fun getNotifications(): Flow<List<NotificationEntity>>

    @Query("UPDATE notifications SET seen = true WHERE id = :notificationId")
    suspend fun makeSeenNotification(notificationId: String)

    @Query("UPDATE notifications SET seen = true")
    suspend fun makeSeenNotifications()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("DELETE FROM notifications")
    suspend fun deleteNotifications()
}