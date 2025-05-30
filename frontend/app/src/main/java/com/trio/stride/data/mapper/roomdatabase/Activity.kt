package com.trio.stride.data.mapper.roomdatabase

import com.trio.stride.data.local.entity.ActivityEntity
import com.trio.stride.data.local.entity.ActivityWithSport
import com.trio.stride.domain.model.ActivityItem
import com.trio.stride.domain.model.ActivityUser

fun ActivityItem.toEntity(): ActivityEntity = ActivityEntity(
    id = id,
    name = name,
    sportId = sport.id,
    totalDistance = totalDistance,
    elevationGain = elevationGain,
    movingTimeSeconds = movingTimeSeconds,
    mapImage = mapImage,
    createdAt = createdAt
)

fun ActivityWithSport.toActivityItem(user: ActivityUser): ActivityItem = ActivityItem(
    id = activity.id,
    name = activity.name,
    sport = sport.toModel(),
    totalDistance = activity.totalDistance,
    elevationGain = activity.elevationGain,
    movingTimeSeconds = activity.movingTimeSeconds,
    mapImage = activity.mapImage,
    createdAt = activity.createdAt,
    user = user
)