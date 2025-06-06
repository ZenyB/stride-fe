package com.trio.stride.ui.screens.progress.detail

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMaxOfOrNull
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.trio.stride.data.mapper.roomdatabase.total
import com.trio.stride.domain.model.Progress
import com.trio.stride.domain.model.ProgressTimeRange
import com.trio.stride.domain.model.ProgressType
import com.trio.stride.domain.model.SportMapType
import com.trio.stride.domain.model.isEmpty
import com.trio.stride.navigation.Screen
import com.trio.stride.ui.components.CustomLeftTopAppBar
import com.trio.stride.ui.components.goal.FilledRadioButtons
import com.trio.stride.ui.components.goal.OutlinedRadioButtonsHorizontal
import com.trio.stride.ui.components.progress.ActivityBottomSheet
import com.trio.stride.ui.components.progress.ProgressChart
import com.trio.stride.ui.components.progress.ProgressDetailSkeleton
import com.trio.stride.ui.components.progress.formatWeekRange
import com.trio.stride.ui.components.progress.getLast12WeeksLabels
import com.trio.stride.ui.components.progress.getLastDaysChartData
import com.trio.stride.ui.components.progress.getLastMonthChartData
import com.trio.stride.ui.components.progress.getLastWeeksChartData
import com.trio.stride.ui.components.progress.getYearToCurrentWeekChartData
import com.trio.stride.ui.components.sport.bottomsheet.SportMapBottomSheet
import com.trio.stride.ui.components.sport.buttonchoosesport.ChooseSportInProgress
import com.trio.stride.ui.screens.progress.roundToHalf
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.formatDistance
import com.trio.stride.ui.utils.formatDuration
import java.util.Locale
import kotlin.math.ceil

@Composable
fun ProgressDetailScreen(
    navController: NavController,
    viewModel: ProgressDetailViewModel = hiltViewModel(),
    activityViewModel: ProgressActivityViewModel = hiltViewModel()
) {
    val progressItems = viewModel.progressData.collectAsStateWithLifecycle()
    val activityItems = activityViewModel.activityData.collectAsStateWithLifecycle()

    val activityUiState by activityViewModel.uiState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sportList by viewModel.sportList.collectAsStateWithLifecycle()

    var showSportSheet by remember { mutableStateOf(false) }
    var showActivitySheet by remember { mutableStateOf(false) }

    var normalizedItems by remember { mutableStateOf(emptyList<Progress>()) }

    LaunchedEffect(Unit) {
        viewModel.initData()
    }
    LaunchedEffect(uiState.sport) {
        viewModel.getProgressDetail()
    }

    Scaffold(
        containerColor = StrideTheme.colorScheme.surface,
        topBar = {
            CustomLeftTopAppBar(
                title = "Progress",
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .background(
                                color = StrideTheme.colors.white,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        }) { padding ->
        Column(
            Modifier
                .padding(top = padding.calculateTopPadding() + 24.dp, bottom = 24.dp)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (uiState.state) {
                LoadingState.Loading -> ProgressDetailSkeleton()
                else -> {
                    OutlinedRadioButtonsHorizontal(
                        options = ProgressType.entries,
                        selectedOption = uiState.selectedFilterType,
                        onOptionSelected = {
                            viewModel.onTypeSelected(it)
                        }
                    ) {
                        uiState.sport?.let {
                            ChooseSportInProgress(
                                iconImage = uiState.sport!!.image,
                                onClick = {
                                    showSportSheet = true
                                },
                                sport = it,
                            )
                        }
                    }

                    val isClickable =
                        uiState.selectedIndex != null && normalizedItems.getOrNull(uiState.selectedIndex!!) != null
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            if (isClickable) {
                                val selectedItem = normalizedItems[uiState.selectedIndex!!]
                                Text(
                                    uiState.selectedFilterType.name.lowercase(Locale.ROOT)
                                        .replaceFirstChar { c -> c.uppercaseChar() },
                                    style = StrideTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    viewModel.getSelectedValue(
                                        uiState.selectedFilterType,
                                        selectedItem
                                    ),
                                    style = StrideTheme.typography.displaySmall
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    formatWeekRange(
                                        selectedItem.fromDate,
                                        selectedItem.toDate,
                                    ),
                                    style = StrideTheme.typography.bodySmall,
                                    color = StrideTheme.colors.gray600
                                )
                            } else if (normalizedItems.isNotEmpty()) {
                                val totalItem =
                                    remember(normalizedItems) { normalizedItems.total() }
                                Text(
                                    "Total ${
                                        uiState.selectedFilterType.name.lowercase(Locale.ROOT)
                                            .replaceFirstChar { c -> c.uppercaseChar() }
                                    }",
                                    style = StrideTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    viewModel.getSelectedValue(
                                        uiState.selectedFilterType,
                                        totalItem
                                    ),
                                    style = StrideTheme.typography.displaySmall
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    formatWeekRange(
                                        totalItem.fromDate,
                                        totalItem.toDate,
                                    ),
                                    style = StrideTheme.typography.bodySmall,
                                    color = StrideTheme.colors.gray600
                                )
                            }
                        }

                        if (isClickable && normalizedItems[uiState.selectedIndex!!].numberActivities > 0) {
                            val selectedItem = normalizedItems[uiState.selectedIndex!!]
                            IconButton(
                                onClick = {
                                    showActivitySheet = true
                                    uiState.sport?.let {
                                        activityViewModel.getProgressActivity(
                                            it.id,
                                            selectedItem.fromDate,
                                            selectedItem.toDate
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .background(
                                        color = StrideTheme.colors.white,
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "View more",
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                        }
                    }
                    var xStep: Double = 1.0

                    if (progressItems.value != null && !progressItems.value!!.isEmpty()) {
                        Log.d("Progress chart", "chart data ${progressItems.value}")
                        when (uiState.selectedTimeFrame) {
                            ProgressTimeRange.LAST_7_DAYS -> {
                                normalizedItems = getLastDaysChartData(progressItems.value!!.n7D!!)
                            }

                            ProgressTimeRange.LAST_1_MONTH -> {
                                normalizedItems =
                                    getLastMonthChartData(progressItems.value!!.n1M!!)
                                xStep = 7.0
                            }

                            ProgressTimeRange.LAST_3_MONTHS -> {
                                normalizedItems =
                                    getLastWeeksChartData(progressItems.value!!.n3M!!, 12)
                                xStep = if (normalizedItems.size > 24) 12.0 else 4.0
                            }

                            ProgressTimeRange.LAST_6_MONTHS -> {
                                normalizedItems =
                                    getLastWeeksChartData(progressItems.value!!.n6M!!, 24)
                                xStep = if (normalizedItems.size > 24) 12.0 else 4.0
                            }

                            ProgressTimeRange.YEAR_TO_DATE -> {
                                normalizedItems =
                                    getYearToCurrentWeekChartData(progressItems.value!!.ytd!!)
                                xStep = if (normalizedItems.size > 24) 12.0 else 4.0
                            }

                            ProgressTimeRange.LAST_1_YEAR -> {
                                normalizedItems =
                                    getLastWeeksChartData(progressItems.value!!.n1Y!!, 52)
                                xStep = if (normalizedItems.size > 24) 12.0 else 4.0
                            }

                        }
                        val yFormatter: CartesianValueFormatter
                        var maxDistance: Double? = null
                        when (uiState.selectedFilterType) {
                            ProgressType.DISTANCE -> {
                                yFormatter = CartesianValueFormatter { _, value, _ ->
                                    val formatted = "${formatDistance(value)} km"
                                    formatted
                                }
                                maxDistance = normalizedItems.fastMaxOfOrNull { it.distance } ?: 5.0
                            }

                            ProgressType.ELEVATION -> {
                                yFormatter = CartesianValueFormatter { _, value, _ ->
                                    val formatted = "$value m"
                                    formatted
                                }
                                maxDistance =
                                    normalizedItems.fastMaxOfOrNull { it.elevation.toDouble() }
                                        ?: 5.0
                            }

                            ProgressType.TIME -> {
                                yFormatter = CartesianValueFormatter { _, value, _ ->
                                    val formatted = formatDuration(value.toLong())
                                    formatted
                                }
                                maxDistance =
                                    ((normalizedItems.fastMaxOfOrNull { it.time.toDouble() }
                                        ?: 60.0).let { ceil(it) } / 30).let { ceil(it) * 30 }
                            }

                            ProgressType.ACTIVITY -> {
                                yFormatter = CartesianValueFormatter { _, value, _ ->
                                    value.toInt().toString()
                                }
                                maxDistance =
                                    ((normalizedItems.fastMaxOfOrNull { it.numberActivities.toDouble() }
                                        ?: 3.0).let { ceil(it) } / 3).let { ceil(it) * 3 }

                            }
                        }

                        var labels: List<String> =
                            getLast12WeeksLabels(normalizedItems, uiState.selectedTimeFrame)

                        val yStepValue: Double =
                            roundToHalf(((ceil(maxDistance).coerceAtLeast(1.0)) / 3))
                        ProgressChart(
                            items = normalizedItems,
                            yStep = yStepValue,
                            xStep = xStep,
                            labels = labels,
                            yFormatter = yFormatter,
                            filterType = uiState.selectedFilterType,
                            hasMarker = true
                        ) { index ->
                            viewModel.selectIndex(index)
                        }
                    } else {
                        normalizedItems = emptyList()
                    }
                    FilledRadioButtons(
                        options = ProgressTimeRange.entries,
                        selectedOption = uiState.selectedTimeFrame,
                        onOptionSelected = {
                            viewModel.selectTimeRange(it)
                        }
                    )
                }
            }
        }
    }
    SportMapBottomSheet(
        sports = sportList,
        selectedSport = uiState.sport,
        visible = showSportSheet,
        onItemClick = {
            viewModel.selectSport(it)
            showSportSheet = false
        },
        dismissAction = { showSportSheet = false },
    )

    if (showActivitySheet) {
        ActivityBottomSheet(
            onDismiss = { showActivitySheet = false },
            uiState = activityUiState,
            item = activityItems.value,
            sportImage = uiState.sport?.image,
            title = formatWeekRange(
                normalizedItems[uiState.selectedIndex!!].fromDate,
                normalizedItems[uiState.selectedIndex!!].toDate
            ),
            onItemSelected = { id ->
                if (uiState.sport?.sportMapType != SportMapType.NO_MAP) {
                    navController.navigate(Screen.ActivityDetail.createRoute(id))
                } else {
                    navController.navigate(Screen.ActivityDetailNoMap.createRoute(id))
                }
            },
        )
    }
}