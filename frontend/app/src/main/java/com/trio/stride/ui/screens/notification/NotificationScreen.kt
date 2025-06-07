package com.trio.stride.ui.screens.notification

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trio.stride.R
import com.trio.stride.ui.components.CustomLeftTopAppBar
import com.trio.stride.ui.components.notification.NotificationItemSkeleton
import com.trio.stride.ui.components.notification.NotificationItemView
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.RequestNotificationPermission
import com.trio.stride.ui.utils.advancedShadow

@Composable
fun NotificationScreen(
    back: () -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showNotificationRequest by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            lastVisibleItemIndex to totalItemsCount
        }.collect { (lastVisible, total) ->
            if (lastVisible != null && lastVisible >= total - 1 && viewModel.isInitialLoadDone && state.hasNextPage && !state.loadingMore) {
                Log.i("LOAD_MORE", "load more")
                viewModel.loadMore()
            }
        }
    }

    RequestNotificationPermission(
        showRequest = showNotificationRequest,
        onPermissionGranted = {

        },
        onPermissionDenied = {
            showNotificationRequest = false
        }
    )

    AnimatedVisibility(
        modifier = Modifier.fillMaxSize(),
        visible = true,
        enter = slideInVertically(
            initialOffsetY = { -it }
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it }
        )
    ) {
        Scaffold(
            topBar = {
                CustomLeftTopAppBar(
                    title = "Notifications",
                    navigationIcon = {
                        IconButton(
                            onClick = back
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.park_down_icon),
                                contentDescription = "Back",
                                tint = StrideTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(90f)
                            )
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = { viewModel.makeSeenAll() }
                        ) {
                            Text(
                                "Mark all as read",
                                style = StrideTheme.typography.labelLarge.copy(color = StrideTheme.colorScheme.primary)
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            if (state.isLoading) {
                Box(
                    Modifier
                        .padding(top = paddingValues.calculateTopPadding())
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NotificationItemSkeleton(10)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = paddingValues.calculateTopPadding())
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    state = listState
                ) {
                    item {
                        Spacer(Modifier.height(12.dp))
                    }
                    
                    itemsIndexed(state.notifications) { index, notification ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .advancedShadow(cornersRadius = 16.dp, shadowBlurRadius = 5.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    StrideTheme.colorScheme.surface,
                                    RoundedCornerShape(16.dp)
                                )
                        ) {
                            NotificationItemView(
                                onItemClick = { viewModel.makeSeen(notification.id) },
                                user = state.user,
                                notification = notification,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }

                    if (state.hasNextPage && state.loadingMore) {
                        item {
                            NotificationItemSkeleton()
                        }
                    }
                }
            }
        }
    }
}