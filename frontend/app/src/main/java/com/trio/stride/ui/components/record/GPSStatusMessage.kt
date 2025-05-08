package com.trio.stride.ui.components.record

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.trio.stride.ui.components.StatusMessage
import com.trio.stride.ui.components.StatusMessageType
import com.trio.stride.ui.screens.record.RecordViewModel
import kotlinx.coroutines.delay

@Composable
fun GPSStatusMessage(modifier: Modifier = Modifier, gpsStatus: RecordViewModel.GPSStatus) {
    val showMessage = remember { mutableStateOf(true) }

    LaunchedEffect(gpsStatus) {
        if (gpsStatus == RecordViewModel.GPSStatus.GPS_READY) {
            delay(5000)
            showMessage.value = false
        } else {
            showMessage.value = true
        }
    }
    when (gpsStatus) {
        RecordViewModel.GPSStatus.NO_GPS -> {
            StatusMessage("NO GPS SIGNAL", StatusMessageType.ERROR, modifier)
        }

        RecordViewModel.GPSStatus.ACQUIRING_GPS -> {
            StatusMessage("ACQUIRING GPS...", StatusMessageType.PROCESSING, modifier)
        }

        RecordViewModel.GPSStatus.GPS_READY -> {
            if (showMessage.value)
                StatusMessage("GPS SIGNAL ACQUIRED", StatusMessageType.SUCCESS, modifier)
        }
    }
}