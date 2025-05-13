package com.trio.stride.ui.components.activity.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.trio.stride.domain.model.ActivityDetailInfo
import com.trio.stride.ui.components.Avatar
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.formatDate
import com.trio.stride.ui.utils.formatDuration
import com.trio.stride.ui.utils.formatKmDistance
import com.trio.stride.ui.utils.formatSpeed

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActivityDetailView(
    item: ActivityDetailInfo
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StrideTheme.colorScheme.surface)
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp)
            .padding(
                bottom = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding()
            ),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .padding(16.dp)
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
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth(),
                maxItemsInEachRow = 2,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatText(
                    "Distance",
                    "${formatKmDistance(item.totalDistance ?: 0.0)} km",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(0.5f),
                    horizontal = Alignment.CenterHorizontally
                )
                StatText(
                    "Time",
                    formatDuration(item.movingTimeSeconds),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(0.5f),
                    horizontal = Alignment.CenterHorizontally
                )
                StatText(
                    "Carbon Saved",
                    "${item.carbonSaved} kg CO2",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(0.5f),
                    horizontal = Alignment.CenterHorizontally
                )

                StatText(
                    "Avg Speed",
                    "${formatSpeed(item.avgSpeed)} km/h",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(0.4f),
                    horizontal = Alignment.CenterHorizontally
                )

                StatText(
                    "Elevation Gain",
                    "${item.elevationGain}m",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(0.4f),
                    horizontal = Alignment.CenterHorizontally
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(0.4f)
                )
            }
        }

        if (item.sport.sportMapType != null && item.speeds.isNotEmpty()) {
            Spacer(
                Modifier
                    .height(8.dp)
                    .fillMaxWidth()
                    .background(StrideTheme.colors.background)
            )
            SpeedChart(
                item, modifier = Modifier
                    .padding(16.dp)
            )
        }

        if (item.sport.sportMapType != null && item.elevations.isNotEmpty()) {
            Spacer(
                Modifier
                    .height(8.dp)
                    .fillMaxWidth()
                    .background(StrideTheme.colors.background)
            )
            ElevationChart(
                item, modifier = Modifier
                    .padding(16.dp)
            )
        }

        if (item.sport.sportMapType != null && item.heartRates.isNotEmpty()) {
            Spacer(
                Modifier
                    .height(8.dp)
                    .fillMaxWidth()
                    .background(StrideTheme.colors.background)
            )
            HeartRateChart(
                item, modifier = Modifier
                    .padding(16.dp)
            )
        }

        if (item.sport.sportMapType != null && !item.heartRateZones.isNullOrEmpty() && item.heartRates.isNotEmpty()) {
            Spacer(
                Modifier
                    .height(8.dp)
                    .fillMaxWidth()
                    .background(StrideTheme.colors.background)
            )
            HeartRateZonesChart(items = item.heartRateZones)
        }
    }

}
