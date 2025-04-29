package com.trio.stride.ui.components.activity.feelingbottomsheet

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RateFeelingBottomSheetState @Inject constructor(

) : ViewModel() {
    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet

    private val _feelingStatusText = MutableStateFlow("Easy")
    val feelingStatusText: StateFlow<String> = _feelingStatusText

    private val _feelingRate = MutableStateFlow(0)
    val feelingRate: StateFlow<Int> = _feelingRate

    fun updateFeelingRate(value: Int) {
        if (value in (0..10)) {
            _feelingRate.value = value
            when (value) {
                in (0..2) -> _feelingStatusText.value = "Easy"
                in (3..5) -> _feelingStatusText.value = "Moderate"
                in (6..9) -> _feelingStatusText.value = "Hard"
                10 -> _feelingStatusText.value = "Max Effort"
            }
        }
    }

    fun show() {
        _showBottomSheet.value = true
    }

    fun hide() {
        _showBottomSheet.value = false
    }
}