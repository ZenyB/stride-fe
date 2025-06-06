package com.trio.stride.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trio.stride.domain.model.SportMapType

@Entity(tableName = "route_filter_sport")
data class RouteFilterSportEntity(
    @PrimaryKey
    val id: String,
    val categoryId: String,
    val name: String,
    val image: String,
    val color: String,
    val sportMapType: String = SportMapType.NO_MAP.name,
)