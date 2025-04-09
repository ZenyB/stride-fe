package com.trio.stride.data.dto

data class GetUserResponse(
    val id: String,
    val name: String,
    val ava: String,
    val dob: String,
    val height: Int,
    val weight: Int,
    val male: Boolean,
    val city: String,
    val maxHeartRate: Int,
    val equipmentsWeight: EquipmentsWeight,
    val heartRateZones: HeartRateZones,
    val isBlock: Boolean
)

data class EquipmentsWeight(
    val SHOES: Int = 0,
    val BAG: Int = 0
)

data class HeartRateZones(
    val ZONE1: Int = 0,
    val ZONE2: Int = 0,
    val ZONE3: Int = 0,
    val ZONE4: Int = 0,
    val ZONE5: Int = 0,
)