package com.trio.stride.ui.utils.map

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.trio.stride.ui.utils.PermissionViewModel
import kotlinx.coroutines.launch

@Composable
fun RequestLocationPermission(
    onPermissionDenied: () -> Unit,
    onPermissionReady: () -> Unit,
    showRequest: Boolean = true,
    viewModel: PermissionViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val permissionCount by viewModel.locationCount.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissionsMap ->
        val granted = permissionsMap.values.all { it }

        scope.launch {
            if (permissionCount >= PermissionViewModel.MAX_REQUEST_COUNT) {
                viewModel.resetLocationCount()
            }
            if (granted) {
                if (permissionCount > 0) {
                    viewModel.resetLocationCount()
                }
                onPermissionReady()
            } else {
                viewModel.plusLocationCount()
                onPermissionDenied()
            }
        }
    }
    LaunchedEffect(permissionCount, showRequest) {
        if (permissionCount < PermissionViewModel.MAX_REQUEST_COUNT && showRequest) {
            context.checkAndRequestLocationPermission(
                locationPermissions,
                launcher,
                onPermissionReady
            )
        }
    }
}

private fun Context.checkAndRequestLocationPermission(
    permissions: Array<String>,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    onPermissionReady: () -> Unit
) {
    if (permissions.all {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    ) {
        onPermissionReady()
    } else {
        launcher.launch(permissions)
    }
}

private val locationPermissions = arrayOf(
    android.Manifest.permission.ACCESS_FINE_LOCATION,
    android.Manifest.permission.ACCESS_COARSE_LOCATION
)