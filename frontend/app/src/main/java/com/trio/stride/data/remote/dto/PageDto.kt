package com.trio.stride.data.remote.dto

data class PageDto(
    val index: Int = 1,
    val limit: Int = 10,
    val totalElements: Int = 1,
    val totalPages: Int = 1
)