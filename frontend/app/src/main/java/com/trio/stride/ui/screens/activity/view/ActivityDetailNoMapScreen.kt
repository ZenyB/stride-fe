package com.trio.stride.ui.screens.activity.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trio.stride.domain.model.Activity
import com.trio.stride.ui.components.CustomLeftTopAppBar
import com.trio.stride.ui.components.LoadingSmall
import com.trio.stride.ui.components.activity.detail.ActivityActionDropdown
import com.trio.stride.ui.components.activity.detail.ActivityDetailView
import com.trio.stride.ui.components.dialog.StrideDialog
import com.trio.stride.ui.screens.activity.detail.ActivityFormMode
import com.trio.stride.ui.screens.activity.detail.ActivityFormView
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun ActivityDetailNoMapScreen(
    id: String = "",
    navController: NavController,
    viewModel: ActivityDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val item by viewModel.item.collectAsStateWithLifecycle()
    var showDiscardEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.getActivityDetail(id)
    }

    StrideDialog(
        visible = showDiscardEditDialog,
        title = "Discard Unsaved Change",
        subtitle = "Your changes will not be saved.",
        dismiss = { showDiscardEditDialog = false },
        destructiveText = "Discard",
        destructive = { viewModel.discardEdit() },
        dismissText = "Cancel"
    )

    StrideDialog(
        visible = showDeleteDialog,
        title = "Delete Activity",
        subtitle = "Your activity will be permanently deleted.",
        dismiss = { showDeleteDialog = false },
        destructiveText = "Delete",
        destructive = {
            showDeleteDialog = false
            navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
            viewModel.deleteActivity()
        },
        dismissText = "Cancel"
    )

    if (uiState is ActivityDetailState.Deleted) {
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            CustomLeftTopAppBar(
                title = item?.sport?.name ?: "Activity",
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
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (uiState is ActivityDetailState.Loading || uiState is ActivityDetailState.Error) {
                            LoadingSmall()
                        } else {
                            ActivityActionDropdown(
                                handleDelete = { showDeleteDialog = true },
                                handleEdit = { viewModel.openEditView() }
                            )
                        }
                    }
                }
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(top = padding.calculateTopPadding())) {
            when (uiState) {
                is ActivityDetailState.Loading -> {
                    Box(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingSmall()
                    }
                }

                is ActivityDetailState.Idle -> {
                    item?.let { ActivityDetailView(it) }
                }

                is ActivityDetailState.Error -> {
                    Box(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            (uiState as ActivityDetailState.Error).message,
                            style = StrideTheme.typography.titleMedium,
                            color = StrideTheme.colors.red700
                        )
                    }
                }

                else -> {}
            }
        }
    }

    AnimatedVisibility(
        uiState == ActivityDetailState.Edit,
        enter = slideInVertically(
            initialOffsetY = { it }
        ),
        exit = slideOutVertically(
            targetOffsetY = { it }
        )
    ) {
        ActivityFormView(
            "Edit Activity",
            "DONE",
            mode = ActivityFormMode.Update(
                activity = if (item != null) {
                    Activity(
                        id = item!!.id,
                        mapImage = item!!.mapImage,
                        images = item!!.images.map { it.toString() },
                        name = item!!.name,
                        description = item!!.description,
                        sport = item!!.sport,
                        rpe = item!!.rpe.toInt(),
                    )
                } else Activity(),
                onUpdate = { dto, sport ->
                    viewModel.updateActivity(
                        dto,
                        sport,
                        {
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "refresh",
                                true
                            )
                        })
                },
                onDiscard = {
                    showDiscardEditDialog = true
                }
            ),
            dismissAction = { showDiscardEditDialog = true },
            isSaving = uiState == ActivityDetailState.Loading,
        )
    }
}

