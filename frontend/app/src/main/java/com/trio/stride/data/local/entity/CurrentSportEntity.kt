package com.trio.stride.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "current_sport")
data class CurrentSportEntity(
    @PrimaryKey
    val id: String,
    val categoryId: String,
    val name: String,
    val image: String,
    val sportMapType: String? = null,
)