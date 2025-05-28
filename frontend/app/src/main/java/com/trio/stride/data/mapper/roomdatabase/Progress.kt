package com.trio.stride.data.mapper.roomdatabase

import com.trio.stride.data.local.entity.ProgressEntity
import com.trio.stride.domain.model.Progress

fun ProgressEntity.toModel(): Progress = Progress(
    fromDate = this.fromDate,
    toDate = this.toDate,
    distance = this.distance,
    elevation = this.elevation,
    time = this.time,
    numberActivities = this.numberActivities
)

fun List<ProgressEntity>.toSportList(): List<Progress> {
    return this.map { it.toModel() }
}
fun List<Progress>.total(): Progress {
    return this.reduce { acc, progress ->
        Progress(
            fromDate = minOf(acc.fromDate, progress.fromDate),
            toDate = maxOf(acc.toDate, progress.toDate),
            distance = acc.distance + progress.distance,
            elevation = acc.elevation + progress.elevation,
            time = acc.time + progress.time,
            numberActivities = acc.numberActivities + progress.numberActivities
        )
    }
}