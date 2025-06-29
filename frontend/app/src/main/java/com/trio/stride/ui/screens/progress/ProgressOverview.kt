package com.trio.stride.ui.screens.progress

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMaxOfOrNull
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.trio.stride.domain.model.ProgressTimeRange
import com.trio.stride.domain.model.ProgressType
import com.trio.stride.domain.model.SportMapType
import com.trio.stride.navigation.Screen
import com.trio.stride.ui.components.activity.detail.StatText
import com.trio.stride.ui.components.progress.ProgressChart
import com.trio.stride.ui.components.progress.ProgressOverviewSkeleton
import com.trio.stride.ui.components.progress.formatWeekRange
import com.trio.stride.ui.components.progress.getLast12WeeksLabels
import com.trio.stride.ui.components.progress.getLastWeeksChartData
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.formatDistance
import com.trio.stride.ui.utils.formatDuration
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
fun ProgressOverview(
    navController: NavController,
    viewModel: ProgressOverviewViewModel = hiltViewModel()
) {
    val progressItems = viewModel.progressData.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sportList by viewModel.sportList.collectAsStateWithLifecycle()
    val sportItem = sportList.firstOrNull { it.id == uiState.sportId }

    val normalizedItems = getLastWeeksChartData(progressItems.value, 12)

    LaunchedEffect(uiState.sportId) {
        viewModel.getProgress()
    }

    LaunchedEffect(Unit) {
        viewModel.initData()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .background(StrideTheme.colorScheme.surface)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Log.d("ProgressIndex", "ui index: ${uiState.selectedIndex}")
        when (uiState.state) {
            OverviewLoadingState.Idle -> {
                if (uiState.selectedIndex != null && normalizedItems.getOrNull(uiState.selectedIndex!!) != null) {
                    val selectedItem = normalizedItems[uiState.selectedIndex!!]

                    Text(
                        formatWeekRange(
                            selectedItem.fromDate,
                            selectedItem.toDate,
                        ),
                        style = StrideTheme.typography.titleLarge
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        StatText("Distance", "${formatDistance(selectedItem.distance)} km")
                        StatText("Time", formatDuration(selectedItem.time))
                        StatText("Elev Gain", "${selectedItem.elevation} m")
                    }
                } else {
                    Text(
                        "_ _",
                        style = StrideTheme.typography.titleLarge
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        StatText("Distance", "_")
                        StatText("Time", "_")
                        StatText("Elev Gain", "_")
                    }
                }

                uiState.sportId?.let {
                    SportHorizontalList(
                        sportList,
                        selectedId = it,
                        onOptionSelected = { sportId ->
                            viewModel.selectSport(sportId)
                        }
                    )
                }

                if (progressItems.value.isNotEmpty()) {
                    val maxDistance = normalizedItems.fastMaxOfOrNull { it.distance } ?: 5.0
                    val yStepValue: Double =
                        roundToHalf(((ceil(maxDistance).coerceAtLeast(1.0)) / 2))


                    Log.d("Progress chart", "max distance: $maxDistance")
                    Log.d("Progress chart", "yStepValue: $yStepValue")

                    val sportType = sportItem?.sportMapType
                    val yFormatter = when (sportType) {
                        SportMapType.NO_MAP -> CartesianValueFormatter { _, value, _ ->
                            val formatted = formatDuration(value.toLong())
                            formatted
                        }

                        else -> CartesianValueFormatter { _, value, _ ->
                            val formatted = "${formatDistance(value)} km"
                            formatted
                        }
                    }
                    val labels =
                        getLast12WeeksLabels(normalizedItems, ProgressTimeRange.LAST_3_MONTHS)
                    ProgressChart(
                        items = normalizedItems,
                        yStep = yStepValue,
                        yFormatter = yFormatter,
                        labels = labels,
                        filterType = if (sportType == SportMapType.NO_MAP) ProgressType.TIME else ProgressType.DISTANCE
                    ) { index ->
                        viewModel.selectIndex(index)
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate(
                            Screen.ProgressDetailScreen.route
                        )
                    }
                ) {
                    Text("See more of your progress")
                }
            }

            OverviewLoadingState.Loading -> ProgressOverviewSkeleton()
            else -> {}
        }
    }
}

fun roundToHalf(value: Double): Double {
    return (value * 2).roundToInt() / 2.0
}