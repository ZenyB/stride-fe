package com.trio.stride.domain.model

data class RouteItem(
    val _id: String,
    val sportId: String,
    val name: String,
    val avgTime: Long,
    val avgDistance: Double,
    val totalTime: Long,
    val location: RouteLocation,
    val images: List<String>?,
    val mapImage: String,
    val geometry: String
)

data class RouteLocation(
    val ward: String?,
    val district: String?,
    val city: String?,
)

fun RouteLocation.toFormattedString(): String {
    return listOfNotNull(ward, district, city).joinToString(", ")
}