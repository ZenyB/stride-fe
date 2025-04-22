package com.trio.stride.ui.utils.map

import com.mapbox.geojson.Point

internal object CityLocations {
    val HCM: Point = Point.fromLngLat(106.7017563, 10.775844)

    val SamplePoints = listOf(
        Pair(
            Point.fromLngLat(106.700981, 10.775659),
            Point.fromLngLat(106.692452, 10.774917)
        ),
        Pair(
            Point.fromLngLat(106.695850, 10.782732),
            Point.fromLngLat(106.695081, 10.776899)
        ),
        Pair(
            Point.fromLngLat(106.697556, 10.772016),
            Point.fromLngLat(106.693381, 10.768793)
        ),
        Pair(
            Point.fromLngLat(106.712105, 10.790768),
            Point.fromLngLat(106.695194, 10.779783)
        ),
        Pair(
            Point.fromLngLat(106.721780, 10.794157),
            Point.fromLngLat(106.716560, 10.796592)
        )
    )
}