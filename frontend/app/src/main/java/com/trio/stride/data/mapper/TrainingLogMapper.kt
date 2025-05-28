package com.trio.stride.data.mapper

import com.trio.stride.data.remote.dto.TrainingLogActivityDto
import com.trio.stride.data.remote.dto.TrainingLogItemDto
import com.trio.stride.data.remote.dto.TrainingLogSportDto
import com.trio.stride.domain.model.TrainingLogActivity
import com.trio.stride.domain.model.TrainingLogItem
import com.trio.stride.domain.model.TrainingLogSport

fun TrainingLogItemDto.toModel() = TrainingLogItem(
    date = this.date,
    color = this.color,
    activities = this.activities.map { it.toModel() },
    distance = this.distance,
    elevation = this.elevation,
    time = this.time
)

fun TrainingLogActivityDto.toModel() = TrainingLogActivity(
    id = this.id,
    name = this.name,
    date = this.date,
    sport = this.sport.toModel(),
    distance = this.distance,
    elevation = this.elevation,
    time = this.time
)

fun TrainingLogSportDto.toModel() = TrainingLogSport(
    id = this.id,
    name = this.name,
    image = this.image,
    sportMapType = this.sportMapType
)