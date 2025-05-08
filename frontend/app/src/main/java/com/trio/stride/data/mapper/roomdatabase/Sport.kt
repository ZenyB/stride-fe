package com.trio.stride.data.mapper.roomdatabase

import com.trio.stride.data.local.entity.CurrentSportEntity
import com.trio.stride.data.local.entity.RouteFilterSportEntity
import com.trio.stride.data.local.entity.SportEntity
import com.trio.stride.domain.model.Category
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.model.SportMapType

fun SportEntity.toModel(category: Category): Sport = Sport(
    id = this.id,
    category = category,
    name = this.name,
    image = this.image,
    sportMapType = this.sportMapType?.let { type ->
        SportMapType.entries.firstOrNull {
            it.name == type
        }
    }
)

fun Sport.toEntity(): SportEntity = SportEntity(
    id = this.id,
    categoryId = this.category.id,
    name = this.name,
    image = this.image,
    sportMapType = this.sportMapType?.name
)

fun CurrentSportEntity.toSportEntity(): SportEntity =
    SportEntity(id, categoryId, name, image, sportMapType)

fun SportEntity.toCurrentSportEntity(): CurrentSportEntity =
    CurrentSportEntity(id, categoryId, name, image, sportMapType)

fun RouteFilterSportEntity.toSportEntity(): SportEntity =
    SportEntity(id, categoryId, name, image, sportMapType)

fun SportEntity.toRouteFilterSportEntity(): RouteFilterSportEntity =
    RouteFilterSportEntity(id, categoryId, name, image, sportMapType)