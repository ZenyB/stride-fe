package com.trio.stride.ui.components.activity

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trio.stride.domain.model.ActivityDetailInfo
import com.trio.stride.domain.model.ActivityUser
import com.trio.stride.domain.model.Category
import com.trio.stride.domain.model.HeartRateZones
import com.trio.stride.domain.model.Sport
import com.trio.stride.ui.components.Avatar
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.formatDate
import com.trio.stride.ui.utils.formatDuration
import com.trio.stride.ui.utils.formatKmDistance
import com.trio.stride.ui.utils.formatSpeed

@Composable
fun ActivityDetailView(item: ActivityDetailInfo) {
    Column {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.padding(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Avatar(
                    ava = item.user.ava, name = item.user.name
                )

                Column {
                    Text(item.user.name, style = StrideTheme.typography.titleMedium)
                    Text(
                        formatDate(item.createdAt),
                        style = StrideTheme.typography.bodySmall,
                        color = StrideTheme.colors.gray600
                    )
                }
            }

            Text(
                item.name,
                style = StrideTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    StatText(
                        "Distance",
                        "${formatKmDistance(item.totalDistance ?: 0.0)} km",
                        modifier = Modifier.fillMaxWidth(),
                        horizontal = Alignment.CenterHorizontally
                    )
                }
                item {
                    StatText(
                        "Time",
                        "${formatDuration(item.movingTimeSeconds)} km",
                        modifier = Modifier.fillMaxWidth(),
                        horizontal = Alignment.CenterHorizontally
                    )
                }
                item {
                    StatText(
                        "Carbon Saved",
                        "${item.carbonSaved} kg CO2",
                        modifier = Modifier.fillMaxWidth(),
                        horizontal = Alignment.CenterHorizontally
                    )
                }

                item {
                    StatText(
                        "Avg Speed",
                        "${formatSpeed(item.avgSpeed)} km/h",
                        modifier = Modifier.fillMaxWidth(),
                        horizontal = Alignment.CenterHorizontally
                    )
                }
                item {
                    StatText(
                        "Elevation Gain",
                        "${item.elevationGain}m",
                        modifier = Modifier.fillMaxWidth(),
                        horizontal = Alignment.CenterHorizontally
                    )
                }
            }
        }

        if (item.isNeedMap && item.speeds.isNotEmpty()) {
            Spacer(
                Modifier
                    .height(8.dp)
                    .fillMaxWidth()
                    .background(StrideTheme.colors.background)
            )
            SpeedChart(item, modifier = Modifier.padding(16.dp))
        }

        if (item.isNeedMap && item.elevations.isNotEmpty()) {
            Spacer(
                Modifier
                    .height(8.dp)
                    .fillMaxWidth()
                    .background(StrideTheme.colors.background)
            )
            ElevationChart(item, modifier = Modifier.padding(16.dp))
        }

        if (item.isNeedMap && item.heartRates.isNotEmpty()) {
            Spacer(
                Modifier
                    .height(8.dp)
                    .fillMaxWidth()
                    .background(StrideTheme.colors.background)
            )
            HeartRateChart(item, modifier = Modifier.padding(16.dp))
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewDetail() {
    val item = ActivityDetailInfo(
        id = "5",
        name = "Evening Running",
        description = "1 ngay tuyet doi",
        sport = Sport(
            id = "f0c665ef-eecf-4fb9-a24c-a2179791c1c3",
            category = Category(),
            name = "Run",
            image = "",
            sportMapType = ""
        ),
        user = ActivityUser(id = "10", name = "Mark", ava = ""),
        totalDistance = 0.37,
        elapsedTimeSeconds = 433,
        movingTimeSeconds = 261,
        calories = 15,
        carbonSaved = 189.7,
        rpe = 5,
        coordinates = listOf(
            listOf(
                106.743, 10.870905
            ), listOf(
                106.742999, 10.870904
            ), listOf(
                106.742998, 10.870904
            ), listOf(
                106.742997, 10.870904
            ), listOf(
                106.742996, 10.870904
            ), listOf(
                106.742995, 10.870903
            ), listOf(
                106.743319, 10.871435
            ), listOf(
                106.743325, 10.871361
            ), listOf(
                106.743138, 10.871144
            ), listOf(
                106.743095, 10.871127
            ), listOf(
                106.743025, 10.871115
            ), listOf(
                106.743002, 10.87084
            ), listOf(
                106.743009, 10.87084
            )
        ),
        images = listOf(),
        mapImage = "",
        elevations = listOf(14, 14, 14, 14, 8, 14, 14),
        elevationGain = 24,
        maxElevation = 14,
        speeds = listOf(
            0.0, 0.4003, 0.41658, 0.4003, 0.0,
            0.41658, 0.4003, 0.41658, 0.11532, 0.4003,
            7.43792, 4.87097, 7.60661, 7.82634, 10.59087, 3.61009,
            0.34596, 1.13692, 2.41286, 1.28645, 2.43331, 1.90815, 3.64856,
        ),
        avgSpeed = 5.1,
        maxSpeed = 14.55958,
        heartRates = listOf(
            96, 94, 92, 92, 91, 92, 93, 94, 95, 95, 96, 96,
        ),
        heartRateZones = HeartRateZones(zone1 = 0, zone2 = 0, zone3 = 153, zone4 = 254, zone5 = 27),
        avgHearRate = 73.0,
        maxHearRate = 147,
        createdAt = 1745464951917,
        isNeedMap = true
    )

    ActivityDetailView(item)
}


@Preview
@Composable
fun DonutChartPreview() {
    val viewData = DonutChartDataCollection(
        listOf(
            DonutChartData(1200.0f, Color.Red, title = "Food & Groceries"),
            DonutChartData(1500.0f, Color.Blue, title = "Rent"),
            DonutChartData(300.0f, Color.Green, title = "Gas"),
            DonutChartData(700.0f, Color.Cyan, title = "Online Purchases"),
            DonutChartData(300.0f, Color.Magenta, title = "Clothing")
        )
    )
    DonutChart(Modifier.padding(24.dp), data = viewData) { selected ->
        AnimatedContent(targetState = selected) {
            val amount = it?.amount ?: viewData.totalAmount
            val text = it?.title ?: "Total"

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "$${amount}",
                )
                Text(text)
            }
        }
    }
}