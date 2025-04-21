package com.trio.stride.domain.model

data class UserInfo(
    val id: String = "",
    val name: String = "",
    val ava: String = "",
    val dob: String = "",
    val height: Int = 0,
    val weight: Int = 0,
    val male: Boolean = true,
    val city: String = "",
    val maxHeartRate: Int = 0,
    val equipmentsWeight: EquipmentsWeight = EquipmentsWeight(),
    val heartRateZones: HeartRateZones = HeartRateZones(),
    val isBlock: Boolean = false
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