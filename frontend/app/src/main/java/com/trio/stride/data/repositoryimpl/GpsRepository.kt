package com.trio.stride.data.repositoryimpl

import android.content.Context
import com.trio.stride.ui.screens.record.RecordViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GpsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _gpsStatus = MutableStateFlow(RecordViewModel.GPSStatus.NO_GPS)
    val gpsStatus: StateFlow<RecordViewModel.GPSStatus> = _gpsStatus

    fun updateGpsStatus(newValue: RecordViewModel.GPSStatus) {
        _gpsStatus.value = newValue
    }
}