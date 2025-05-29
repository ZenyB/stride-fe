package com.trio.stride.ui.screens.activity

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.trio.stride.navigation.Screen
import com.trio.stride.ui.components.LoadingSmall
import com.trio.stride.ui.components.RefreshIndicator
import com.trio.stride.ui.components.activity.detail.ActivityItemView
import com.trio.stride.ui.theme.StrideTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    navController: NavController,
    content: @Composable (() -> Unit)? = null,
    viewModel: ActivityViewModel = hiltViewModel()
) {
    val items = viewModel.items
    val listState = rememberLazyListState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val state = rememberPullToRefreshState()
    val shouldRefresh = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Boolean>("refresh")
        ?.observeAsState()

    if (shouldRefresh != null) {
        Log.d("refresh activity", "refresh null")
        LaunchedEffect(shouldRefresh.value) {
            Log.d("refresh activity", "refreshing")

            if (shouldRefresh.value == true) {
                Log.d("refresh activity", "start refresh")
                viewModel.getRefreshActivity()
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.remove<Boolean>("refresh")
            }
        }
    }

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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (content != null) {
                item {
                    content()
                }
            }
            items(items.size) { index ->
                ActivityItemView(item = items[index], onClick = { id ->
                    if (items[index].sport.sportMapType != null) {
                        navController.navigate(Screen.ActivityDetail.createRoute(id))
                    } else {
                        navController.navigate(Screen.ActivityDetailNoMap.createRoute(id))
                    }
                })

                if (index >= items.size - 5) {
                    LaunchedEffect(key1 = true) {
                        viewModel.getAllActivity()
                    }
                }
            }

            if (uiState is ActivityListState.Loading && !viewModel.isRefreshing) {
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

            if (uiState is ActivityListState.Error) {
                item {
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
    }
}
