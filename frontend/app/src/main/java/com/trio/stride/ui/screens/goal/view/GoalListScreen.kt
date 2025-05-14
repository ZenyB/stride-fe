package com.trio.stride.ui.screens.goal.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trio.stride.navigation.Screen
import com.trio.stride.ui.components.CustomLeftTopAppBar
import com.trio.stride.ui.components.LoadingSmall
import com.trio.stride.ui.components.RefreshIndicator
import com.trio.stride.ui.components.goal.GoalItemView
import com.trio.stride.ui.theme.StrideTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalListScreen(
    navController: NavController, viewModel: GoalListViewModel = hiltViewModel()
) {
    val items = viewModel.items
    val listState = rememberLazyListState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val state = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            CustomLeftTopAppBar(
                title = "Goals",
                backgroundColor = StrideTheme.colorScheme.surface,
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
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.CreateGoalScreen.route)
                        },
                        modifier = Modifier
                            .background(
                                color = StrideTheme.colors.white,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        }) { padding ->
        Column(
            modifier = Modifier
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                )
        )
        {
            PullToRefreshBox(
                isRefreshing = viewModel.isRefreshing,
                onRefresh = {
                    viewModel.refresh()
                },
                state = state,
                indicator = {
                    RefreshIndicator(
                        state = state,
                        isRefreshing = viewModel.isRefreshing,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(items.size) { index ->
                        GoalItemView(
                            items[index]
                        )
                    }
                    item {
                        Spacer(Modifier.height(0.dp))
                    }

                    if (uiState is UserGoalState.Loading && !viewModel.isRefreshing) {
                        item {
                            Box(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                LoadingSmall()
                            }
                        }
                    }

                    if (uiState is UserGoalState.Error) {
                        item {
                            Box(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    (uiState as UserGoalState.Error).message,
                                    style = StrideTheme.typography.titleMedium,
                                    color = StrideTheme.colors.red700
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}