package com.trio.stride.ui.screens.activity.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trio.stride.ui.components.LoadingSmall
import com.trio.stride.ui.components.activity.detail.ActivityDetailView
import com.trio.stride.ui.screens.activity.ActivityListState
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun ActivityDetailScreen(
    id: String = "",
    navController: NavController,
    viewModel: ActivityDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val item by viewModel.item.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.getActivityDetail(id)
    }

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
                    (uiState as ActivityListState.Error).message,
                    style = StrideTheme.typography.titleMedium,
                    color = StrideTheme.colors.red700
                )
            }
        }
    }
}