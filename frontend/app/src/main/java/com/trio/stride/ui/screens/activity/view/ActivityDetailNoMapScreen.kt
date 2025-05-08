package com.trio.stride.ui.screens.activity.view

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trio.stride.R
import com.trio.stride.ui.components.CustomLeftTopAppBar
import com.trio.stride.ui.components.LoadingSmall
import com.trio.stride.ui.components.activity.detail.ActivityDetailView
import com.trio.stride.ui.theme.StrideTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailNoMapScreen(
    id: String = "",
    navController: NavController,
    viewModel: ActivityDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val item by viewModel.item.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.getActivityDetail(id)
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
                        IconButton(
                            onClick = {
                            },
                            modifier = Modifier
                                .background(
                                    color = StrideTheme.colors.white,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ellipsis_more),
                                contentDescription = "Close Sheet"
                            )
                        }
                    }
                }
            )
        }) { padding ->
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
            }
        }
    }

}

