package com.trio.stride.data.remote.dto

import com.trio.stride.domain.model.SportMapType

data class GetSportResponseDto(
    val data: List<SportDto>,
    val page: PageDto,
    val filterDto: SportFilterDto
)

data class SportDto(
    val id: String,
    val category: CategoryDto?,
    val name: String,
    val image: String,
    val color: String,
    val sportMapType: SportMapType,
    val rules: List<RuleDto>
)

data class RuleDto(
    val expression: String,
    val met: Double
)

data class SportFilterDto(
    val name: String,
    val categoryId: String?
)