package com.trio.stride.domain.model

data class Sport(
    val id: String = "",
    val category: Category = Category(),
    val name: String = "",
    val image: String = "",
    val color: String = "#CCCCCC",
    val sportMapType: SportMapType? = null,
)

enum class SportMapType { WALKING, DRIVING, CYCLING }