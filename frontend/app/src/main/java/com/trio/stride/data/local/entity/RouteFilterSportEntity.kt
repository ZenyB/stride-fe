package com.trio.stride.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_filter_sport")
data class RouteFilterSportEntity(
    @PrimaryKey
    val id: String,
    val categoryId: String,
    val name: String,
    val image: String,
    val sportMapType: String? = null,
)