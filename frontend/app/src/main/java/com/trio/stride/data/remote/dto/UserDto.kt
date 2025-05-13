package com.trio.stride.data.remote.dto

data class GetUserResponse(
    val id: String,
    val name: String? = null,
    val ava: String? = null,
    val dob: String? = null,
    val height: Int? = null,
    val weight: Int? = null,
    val male: Boolean? = null,
    val city: String? = null,
    val maxHeartRate: Int? = null,
    val equipmentsWeight: EquipmentsWeight? = null,
    val heartRateZones: HeartRateZones? = null,
    val isBlock: Boolean? = null
)

data class UpdateUserRequestDto(
    val name: String = "",
    val ava: String? = null,
    val dob: String? = "",
    val height: Int? = 160,
    val weight: Int? = 50,
    val male: Boolean? = false,
    val city: String? = "TPHCM",
    val maxHeartRate: Int? = null,
    val equipmentsWeight: EquipmentsWeight? = null,
    val heartRateZones: HeartRateZones? = null
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