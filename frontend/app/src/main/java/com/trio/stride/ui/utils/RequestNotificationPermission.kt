package com.trio.stride.ui.utils

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.trio.stride.data.datastoremanager.PermissionCountManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun RequestNotificationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    showRequest: Boolean = true,
    viewModel: PermissionViewModel = hiltViewModel()
) {
    val permissionCount by viewModel.pushNotiCount.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            scope.launch {
                if (permissionCount >= PermissionViewModel.MAX_REQUEST_COUNT) {
                    viewModel.resetPushNotiCount()
                }
                if (isGranted) {
                    viewModel.resetPushNotiCount()
                    onPermissionGranted()
                } else {
                    viewModel.plusPushNotiCount()
                    onPermissionDenied()
                }
            }
        }

        LaunchedEffect(permissionCount) {
            if (permissionCount < PermissionViewModel.MAX_REQUEST_COUNT && showRequest) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    } else {
        onPermissionGranted()
    }
}

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val permissionCountManager: PermissionCountManager
) : ViewModel() {

    val locationCount: StateFlow<Int> = permissionCountManager
        .getLocationCount()
        .map { it ?: 0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val pushNotiCount: StateFlow<Int> = permissionCountManager
        .getPushNotiCount()
        .map { it ?: 0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    companion object {
        const val MAX_REQUEST_COUNT = 2
    }

    fun plusLocationCount() {
        viewModelScope.launch {
            permissionCountManager.plusLocationCount()
        }
    }

    fun plusPushNotiCount() {
        viewModelScope.launch {
            permissionCountManager.plusPushNotiCount()
        }
    }

    fun resetLocationCount() {
        viewModelScope.launch {
            permissionCountManager.resetLocationCount()
        }
    }

    fun resetPushNotiCount() {
        viewModelScope.launch {
            permissionCountManager.resetPushNotiCount()
        }
    }
}

