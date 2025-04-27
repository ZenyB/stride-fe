package com.trio.stride.domain.model

data class Sport(
    val id: String,
    val category: Category,
    val name: String,
    val image: String,
    val sportMapType: String,
)