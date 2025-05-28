package com.trio.stride.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "available_sport")
data class AvailableSportEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val image: String,
)