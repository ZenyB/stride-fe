package com.trio.stride.data.remote.dto

data class PageDto(
    val index: Int = 1,
    val limit: Int = 10,
    val totalElements: Int? = null,
    val totalPages: Int? = null
)