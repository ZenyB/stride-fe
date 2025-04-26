package com.trio.stride.ui.components.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MapFallbackScreen(
    isMapAvailable: Boolean,
    permissionRequestCount: Int,
    onRetry: () -> Unit,
    goToSetting: () -> Unit
) {
    if (!isMapAvailable) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.align(Alignment.Center)) {
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = onRetry
                ) {
                    Text("Try again")
                }
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = goToSetting
                ) {
                    Text("Go to Settings")
                }
            }
        }
    }
}
