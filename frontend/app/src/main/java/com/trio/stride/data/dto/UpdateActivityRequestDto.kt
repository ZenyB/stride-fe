package com.trio.stride.data.dto

data class UpdateActivityRequestDto(
    val rpe: Int = 0,
    val sportId: String = "",
    val name: String = "",
    val description: String = "",
    val images: List<String> = emptyList()
)