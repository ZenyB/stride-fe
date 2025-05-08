package com.trio.stride.data.mapper.roomdatabase

import com.trio.stride.data.local.dao.CategoryWithSports
import com.trio.stride.data.local.entity.CategoryEntity
import com.trio.stride.domain.model.Category
import com.trio.stride.domain.model.Sport

fun CategoryEntity.toModel(): Category = Category(
    id = this.id,
    name = this.name,
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = this.id,
    name = this.name,
)


fun List<CategoryWithSports>.toModel(): Map<Category, List<Sport>> =
    this.associate { categoryWithSports ->
        val category = categoryWithSports.category.toModel()

        val sports = categoryWithSports.sports.map { sportEntity ->
            sportEntity.toModel(category)
        }

        category to sports
    }