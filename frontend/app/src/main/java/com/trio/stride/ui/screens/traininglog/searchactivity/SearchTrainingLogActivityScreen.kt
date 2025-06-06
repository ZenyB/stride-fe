package com.trio.stride.ui.screens.traininglog.searchactivity

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trio.stride.R
import com.trio.stride.domain.model.Range
import com.trio.stride.ui.components.traininglog.filteractivity.FilterActivityItem
import com.trio.stride.ui.components.traininglog.filteractivity.FilterChip
import com.trio.stride.ui.components.traininglog.filteractivity.RangeFilterBottomSheet
import com.trio.stride.ui.components.traininglog.filteractivity.SearchView
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun SearchTrainingLogActivityScreen(
    onBack: () -> Unit,
    navigateToActivityDetail: (Boolean) -> Unit,
    viewModel: SearchTrainingLogActivityViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val horizontalScrollState = rememberScrollState()
    val showSportFilter = remember { mutableStateOf(false) }
    val showDistanceFilter = remember { mutableStateOf(false) }
    val showElevationFilter = remember { mutableStateOf(false) }
    val showTimeFilter = remember { mutableStateOf(false) }
    val showDateFilter = remember { mutableStateOf(false) }

    Box {
        Scaffold(
            containerColor = StrideTheme.colorScheme.surface,
            topBar = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        Modifier.height(
                            WindowInsets.statusBars.asPaddingValues().calculateBottomPadding()
                        )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp)
                            .windowInsetsPadding(WindowInsets.statusBars),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp)
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .rotate(90f),
                                    painter = painterResource(R.drawable.park_down_icon),
                                    contentDescription = "Back",
                                    tint = StrideTheme.colorScheme.onBackground
                                )
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        SearchView(
                            query = state.filter.search,
                            onQueryChanged = {},
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(top = paddingValues.calculateTopPadding())
                    .padding(horizontal = 16.dp)
                    .windowInsetsPadding(
                        WindowInsets.navigationBars
                    )
            ) {
                Column(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(horizontalScrollState)
                    ) {
                        FilterChip(
                            contentText = "All",
                            leadingIcons = {
                                Icon(
                                    painter = painterResource(R.drawable.run_icon),
                                    contentDescription = "Sport Filter",
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            onClick = { showSportFilter.value = true }
                        )
                        FilterChip(
                            contentText = "Distance",
                            onClick = { showDistanceFilter.value = true }
                        )
                        FilterChip(
                            contentText = "Elevation",
                            onClick = { showElevationFilter.value = true }
                        )
                        FilterChip(
                            contentText = "Time",
                            onClick = { showTimeFilter.value = true }
                        )
                        FilterChip(
                            contentText = "Dates",
                            onClick = { showDateFilter.value = true }
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider()
                    LazyColumn {
                        item {
                            Spacer(Modifier.height(12.dp))
                        }

                        itemsIndexed(state.currentActivities) { index, activity ->
                            FilterActivityItem(
                                activity = activity,
                                onClick = { navigateToActivityDetail(activity.sport.sportMapType != null) }
                            )
                        }
                    }
                }
            }
        }

        RangeFilterBottomSheet(
            title = "Distance",
            dismiss = { showDistanceFilter.value = false },
            range = Range(0, 80),
            value = state.filter.distance,
            onRangeChanged = { }
        )

        RangeFilterBottomSheet(
            title = "Elevation",
            dismiss = { showDistanceFilter.value = false },
            range = Range(0, 600),
            value = state.filter.elevation,
            onRangeChanged = { }
        )

        RangeFilterBottomSheet(
            title = "Time",
            dismiss = { showDistanceFilter.value = false },
            range = Range(0, 6 * 60 * 60),
            value = state.filter.time,
            onRangeChanged = { }
        )
    }
}