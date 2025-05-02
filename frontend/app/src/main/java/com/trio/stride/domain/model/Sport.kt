package com.trio.stride.domain.model

data class Sport(
    val id: String = "",
    val category: Category = Category(),
    val name: String = "",
    val image: String = "",
    val sportMapType: SportMapType = SportMapType.WALKING,
)

enum class SportMapType { WALKING, DRIVING, CYCLING }