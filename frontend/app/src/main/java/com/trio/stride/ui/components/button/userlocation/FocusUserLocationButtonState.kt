package com.trio.stride.ui.components.button.userlocation

import androidx.lifecycle.ViewModel
import com.trio.stride.data.repositoryimpl.GpsRepository
import com.trio.stride.ui.screens.record.RecordViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FocusUserLocationButtonState @Inject constructor(
    private val gpsRepository: GpsRepository
) : ViewModel() {
    fun updateGpsStatus(status: RecordViewModel.GPSStatus) {
        gpsRepository.updateGpsStatus(status)
    }
}