package com.trio.stride.domain.model

data class UserInfo(
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
    val shoes: Int = 0,
    val bag: Int = 0
)

data class HeartRateZones(
    val zone1: Int = 0,
    val zone2: Int = 0,
    val zone3: Int = 0,
    val zone4: Int = 0,
    val zone5: Int = 0,
    val zone6: Int = 0,
)