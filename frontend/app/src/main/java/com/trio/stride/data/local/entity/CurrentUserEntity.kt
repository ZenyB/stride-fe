package com.trio.stride.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trio.stride.domain.model.EquipmentsWeight
import com.trio.stride.domain.model.HeartRateZones

@Entity(tableName = "current_user")
data class CurrentUserEntity(
    @PrimaryKey
    val entityId: Int = 0,
    val id: String = "",
    val name: String,
    val ava: String,
    val dob: String,
    val height: Int,
    val weight: Int,
    val male: Boolean,
    val city: String,
    val maxHeartRate: Int,
    val isBlock: Boolean,

    @Embedded val equipmentsWeight: EquipmentsWeight = EquipmentsWeight(),

    @Embedded(prefix = "hr_") val heartRateZones: HeartRateZones = HeartRateZones()
)