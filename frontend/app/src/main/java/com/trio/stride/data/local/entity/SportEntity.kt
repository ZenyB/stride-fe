package com.trio.stride.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sport")
data class SportEntity(
    @PrimaryKey
    val id: String,
    val categoryName: String,
    val name: String,
    val image: String,
    val color: String,
    val sportMapType: String? = null,
)