package com.trio.stride.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipment_weight")
data class EquipmentWeightEntity(
    @PrimaryKey val userId: String,
    val shoes: Int,
    val bag: Int
)