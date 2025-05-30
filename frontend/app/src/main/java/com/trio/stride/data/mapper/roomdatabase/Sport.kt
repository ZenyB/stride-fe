package com.trio.stride.data.mapper.roomdatabase

import com.trio.stride.data.local.entity.AvailableSportEntity
import com.trio.stride.data.local.entity.CurrentSportEntity
import com.trio.stride.data.local.entity.RouteFilterSportEntity
import com.trio.stride.data.local.entity.SportEntity
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.model.SportMapType

fun SportEntity.toModel(): Sport = Sport(
    id = this.id,
    categoryName = this.categoryName,
    name = this.name,
    image = this.image,
    color = this.color,
    sportMapType = this.sportMapType?.let { type ->
        SportMapType.entries.firstOrNull {
            it.name == type
        }
    }
)

fun Sport.toEntity(): SportEntity = SportEntity(
    id = this.id,
    categoryName = this.categoryName,
    name = this.name,
    image = this.image,
    color = this.color,
    sportMapType = this.sportMapType?.name
)

fun CurrentSportEntity.toSportEntity(): SportEntity =
    SportEntity(id, categoryName, name, image, color, sportMapType)

fun SportEntity.toCurrentSportEntity(): CurrentSportEntity =
    CurrentSportEntity(id, categoryName, name, image, color, sportMapType)

fun RouteFilterSportEntity.toSportEntity(): SportEntity =
    SportEntity(id, categoryId, name, image, color, sportMapType)

fun SportEntity.toRouteFilterSportEntity(): RouteFilterSportEntity =
    RouteFilterSportEntity(id, categoryName, name, image, color, sportMapType)

fun AvailableSportEntity.toSport(): Sport {
    return Sport(
        id = this.id,
        name = this.name,
        image = this.image,
        sportMapType = SportMapType.entries.firstOrNull {
            it.name == this.sportMapType
        }
    )
}

fun List<AvailableSportEntity>.toSportList(): List<Sport> {
    return this.map { it.toSport() }
}
