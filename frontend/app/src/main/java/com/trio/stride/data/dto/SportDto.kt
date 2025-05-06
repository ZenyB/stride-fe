package com.trio.stride.data.dto

data class GetSportResponseDto(
    val data: SportDto,
    val page: PageDto,
    val filterDto: SportFilterDto
)

data class SportDto(
    val id: String,
    val category: CategoryDto,
    val name: String,
    val image: String,
    val sportMapType: String,
    val rules: List<RuleDto>,
    val isNeedMap: Boolean
)

data class RuleDto(
    val expression: String,
    val met: Double
)

data class SportFilterDto(
    val name: String,
    val categoryId: String?
)