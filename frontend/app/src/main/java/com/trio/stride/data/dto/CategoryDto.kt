package com.trio.stride.data.dto

data class GetCategoryResponseDto(
    val data: List<CategoryDto>,
    val page: PageDto,
    val filterDto: CategoryFilterDto
)

data class CategoryDto(
    val id: String,
    val name: String
)

data class CategoryFilterDto(
    val name: String,
    val categoryId: String?
)