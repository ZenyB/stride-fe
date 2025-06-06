package com.trio.stride.domain.model

data class Sport(
    val id: String = "",
    val categoryName: String = "Foot Sports",
    val name: String = "",
    val image: String = "",
    val color: String = "",
    val sportMapType: SportMapType = SportMapType.NO_MAP,
)

enum class SportMapType { WALKING, DRIVING, CYCLING, NO_MAP }