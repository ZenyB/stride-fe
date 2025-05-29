package com.trio.stride.ui.screens.traininglog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trio.stride.R
import com.trio.stride.domain.model.TrainingLogFilterDataType
import com.trio.stride.ui.components.CustomLeftTopAppBar
import com.trio.stride.ui.components.Loading
import com.trio.stride.ui.components.dialog.StrideDialog
import com.trio.stride.ui.components.traininglog.HorizontalTrainingLogSkeleton
import com.trio.stride.ui.components.traininglog.HorizontalTrainingLogValue
import com.trio.stride.ui.components.traininglog.HorizontalWeekTitles
import com.trio.stride.ui.components.traininglog.TrainingLogActivitiesDialog
import com.trio.stride.ui.components.traininglog.TrainingLogCalendarFilter
import com.trio.stride.ui.components.traininglog.TrainingLogFilterSheet
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.advancedShadow
import com.trio.stride.ui.utils.formatDistance
import com.trio.stride.ui.utils.formatTimeHM
import com.trio.stride.ui.utils.minusNWeeks
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

@Composable
fun TrainingLogScreen(
    onBack: () -> Unit,
    navigateToActivityDetail: (String, Boolean) -> Unit,
    viewModel: TrainingLogViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val sports by viewModel.sports.collectAsStateWithLifecycle()

    val today = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val listState = rememberLazyListState()
    val showFilterSheet = remember { mutableStateOf(false) }
    val showCalendarFilter = remember { mutableStateOf(false) }
    val targetMonth = remember { mutableStateOf<YearMonth?>(null) }

    LaunchedEffect(targetMonth.value) {
        targetMonth.value?.let { month ->
            val targetIndex = viewModel.scrollToTargetMonth(month)
            if (targetIndex >= 0) {
                listState.animateScrollToItem(targetIndex)
                targetMonth.value = null
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            lastVisibleItemIndex to totalItemsCount
        }.collect { (lastVisible, total) ->
            if (lastVisible != null && lastVisible >= total - 1) {
                viewModel.loadMore()
            }
        }
    }

    Box {
        Scaffold(
            containerColor = StrideTheme.colorScheme.surface,
            topBar = {
                CustomLeftTopAppBar(
                    title = "Training Log",
                    hideDivider = true,
                    navigationIcon = {
                        IconButton(
                            onClick = onBack
                        ) {
                            Icon(
                                modifier = Modifier.rotate(90f),
                                painter = painterResource(R.drawable.park_down_icon),
                                tint = StrideTheme.colorScheme.onSurface,
                                contentDescription = "Back icon"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showFilterSheet.value = true },
                            enabled = !state.isLoading
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(24.dp),
                                painter = painterResource(R.drawable.filter),
                                tint = StrideTheme.colorScheme.onSurface,
                                contentDescription = "Filter icon"
                            )
                        }
                        IconButton(
                            onClick = { showCalendarFilter.value = true },
                            enabled = !state.isLoading
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(24.dp),
                                imageVector = Icons.Outlined.DateRange,
                                tint = StrideTheme.colorScheme.onSurface,
                                contentDescription = "Calendar icon"
                            )
                        }
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(24.dp),
                                imageVector = Icons.Outlined.Search,
                                tint = StrideTheme.colorScheme.onSurface,
                                contentDescription = "Search icon"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(top = paddingValues.calculateTopPadding())
            ) {
                HorizontalWeekTitles(
                    modifier = Modifier
                        .advancedShadow(
                            offsetY = 1.dp,
                            shadowBlurRadius = 6.dp
                        )
                        .background(StrideTheme.colorScheme.surface)
                        .padding(top = 16.dp)
                )
                HorizontalDivider(
                    color = StrideTheme.colors.grayBorder.copy(alpha = 0.5f)
                )
                Spacer(Modifier.height(8.dp))
                if (state.isLoading) {
                    val startDates = (0..5).map {
                        state.nextStartDate.minusNWeeks(
                            it
                        )
                    }

                    val endDates = (0..5).map {
                        state.nextStartDate.minusNWeeks(
                            it
                        ) + 6 * 24 * 60 * 60 * 1000
                    }
                    Column(modifier = Modifier.padding(12.dp)) {
                        HorizontalTrainingLogSkeleton(
                            startDates = startDates,
                            endDates = endDates
                        )
                    }
                }
                LazyColumn(modifier = Modifier.padding(12.dp), state = listState) {
                    itemsIndexed(
                        state.currentWeeksInfo,
                        key = { index, weekInfo -> weekInfo.startDate }) { index, weekInfo ->
                        var todayIndex: Int? = null
                        if (today in weekInfo.startDate..weekInfo.endDate) {
                            val millisecondsPerDay = 24 * 60 * 60 * 1000L
                            todayIndex = ((today - weekInfo.startDate) / millisecondsPerDay).toInt()
                        }

                        val weekDataText = when (state.trainingLogFilter.dataType) {
                            TrainingLogFilterDataType.TIME -> {
                                var value: Long = 0
                                weekInfo.data.map { value += it?.time ?: 0 }
                                formatTimeHM(value.toInt())
                            }

                            TrainingLogFilterDataType.DISTANCE -> {
                                var value: Long = 0
                                weekInfo.data.map { value += it?.distance ?: 0 }
                                "${formatDistance(value.toDouble())} km"
                            }

                            TrainingLogFilterDataType.ELEVATION -> {
                                var value: Long = 0
                                weekInfo.data.map { value += it?.elevation ?: 0 }
                                "$value m"
                            }
                        }

                        HorizontalTrainingLogValue(
                            startDate = weekInfo.startDate,
                            endDate = weekInfo.endDate,
                            trainingLogsData = weekInfo.data,
                            weekDataText = weekDataText,
                            onItemClick = { trainingLogItem ->
                                if (trainingLogItem.activities.size == 1)
                                    navigateToActivityDetail(
                                        trainingLogItem.activities[0].id,
                                        trainingLogItem.activities[0].sport.sportMapType != null
                                    )
                                else viewModel.onSelectTrainingLog(trainingLogItem)
                            },
                            todayIndex = todayIndex,
                        )

                        if (state.hasLoadMorePermission || index != state.currentWeeksInfo.lastIndex) {
                            HorizontalDivider()
                            Spacer(Modifier.height(4.dp))
                        }

                        if (index == state.currentWeeksInfo.lastIndex && state.hasLoadMorePermission) {
                            val startDates = (0..2).map { state.nextStartDate.minusNWeeks(it) }

                            val endDates =
                                (0..2).map { state.nextStartDate.minusNWeeks(it) + 6 * 24 * 60 * 60 * 1000 }
                            HorizontalTrainingLogSkeleton(
                                startDates = startDates,
                                endDates = endDates
                            )
                        }
                    }
                }
            }
        }

        TrainingLogFilterSheet(
            visible = showFilterSheet.value,
            onDismiss = { showFilterSheet.value = false },
            sports = sports,
            selectedSports = state.trainingLogFilter.selectedSports,
            dataType = state.trainingLogFilter.dataType,
            onSelectedSportsChange = { sports ->
                viewModel.updateSportsFilter(sports)
            },
            onDataTypeChange = { dataType ->
                viewModel.updateDataTypesFilter(dataType)
            },
        )

        TrainingLogActivitiesDialog(
            visible = state.selectedTrainingLog != null,
            onDismiss = { viewModel.deSelectTrainingLog() },
            date = state.selectedTrainingLog?.date,
            activities = state.selectedTrainingLog?.activities,
            onActivityClick = { activity ->
                navigateToActivityDetail(
                    activity.id,
                    activity.sport.sportMapType != null
                )
            }
        )

        TrainingLogCalendarFilter(
            visible = showCalendarFilter.value,
            onDismiss = { showCalendarFilter.value = false },
            onMonthSelect = {
                targetMonth.value = it
                showCalendarFilter.value = false
            },
            startDate = state.metaData.from
        )

        StrideDialog(
            visible = state.isFetchError,
            title = "Can't Get Training Log",
            description = "Stride can't get training log data now. Please try again or back to Progress.",
            neutralText = "Back",
            dismiss = { },
            neutral = onBack,
            doneText = "Try Again",
            done = {
                viewModel.resetFetchError()
                viewModel.getTrainingLogsData()
            }
        )

        StrideDialog(
            visible = state.isLoadMoreError,
            title = "Can't Get More Training Log",
            description = "Stride can't get more training log data now. Please try again or stop loading more.",
            dismiss = {},
            neutralText = "Stop Loading More",
            neutral = { viewModel.resetLoadMoreError() },
            doneText = "Try Again",
            done = {
                viewModel.resetLoadMoreErrorAndPermission()
                viewModel.loadMore()
            }
        )
    }

    if (state.scrolling) {
        Loading()
    }
}