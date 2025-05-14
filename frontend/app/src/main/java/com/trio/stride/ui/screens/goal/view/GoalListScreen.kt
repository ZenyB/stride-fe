package com.trio.stride.ui.screens.goal.view

import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trio.stride.navigation.Screen
import com.trio.stride.ui.components.CustomLeftTopAppBar
import com.trio.stride.ui.components.Loading
import com.trio.stride.ui.components.LoadingSmall
import com.trio.stride.ui.components.RefreshIndicator
import com.trio.stride.ui.components.dialog.StrideDialog
import com.trio.stride.ui.components.goal.GoalActionsBottomSheet
import com.trio.stride.ui.components.goal.GoalItemView
import com.trio.stride.ui.theme.StrideTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalListScreen(
    navController: NavController, viewModel: GoalListViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val items = viewModel.items
    val listState = rememberLazyListState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val state = rememberPullToRefreshState()

    var showSheet by remember { mutableStateOf(false) }
    var needDelete by remember { mutableStateOf(false) }


    fun dismiss() {
        showSheet = false
    }

    when (uiState) {
        is UserGoalState.SavingError -> {
            StrideDialog(
                visible = true,
                title = "Error saving activity",
                description = (uiState as UserGoalState.SavingError).message,
                dismiss = { viewModel.resetState() },
                dismissText = "OK",
            )
        }

        is UserGoalState.SavingSuccess -> {
            Toast.makeText(context, "Delete goal successfully!", Toast.LENGTH_SHORT).show()
            viewModel.resetState()
        }

        is UserGoalState.Saving -> {
            Loading()
        }

        else -> {

        }
    }

    StrideDialog(
        visible = needDelete,
        title = "Are you sure?",
        description = "You'll no longer be tracking progress toward this goal",
        dismiss = {
            needDelete = false
        },
        dismissText = "Cancel",
        doneText = "Delete",
        done = {
            needDelete = false
            viewModel.deleteGoal()
        }
    )

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
                        ) {
                            viewModel.selectedItemId = items[index].id
                            showSheet = true
                        }
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
    if (showSheet) {
        GoalActionsBottomSheet(onDismiss = { dismiss() },
            onEdit = {},
            onDelete = {
                needDelete = true
            })
    }
}
