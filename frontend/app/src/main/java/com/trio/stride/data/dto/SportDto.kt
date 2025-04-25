package com.trio.stride.data.dto

data class GetSportResponseDto(
    val data: SportDto,
    val page: PageDto,
    val filterDto: FilterDto
)

data class SportDto(
    val id: String,
    val category: CategoryDto,
    val name: String,
    val image: String,
    val sportMapType: String,
    val rules: List<RuleDto>
)

data class CategoryDto(
    val id: String,
    val name: String
)

data class RuleDto(
    val expression: String,
    val met: Double
)

data class FilterDto(
    val name: String,
    val categoryId: String?
)