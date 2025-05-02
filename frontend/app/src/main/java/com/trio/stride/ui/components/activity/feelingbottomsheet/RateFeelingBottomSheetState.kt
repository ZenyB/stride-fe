package com.trio.stride.ui.components.activity.feelingbottomsheet

import androidx.lifecycle.ViewModel
import com.trio.stride.R
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

    private val _feelingDetailTextId = MutableStateFlow(R.string.easy_rpe)
    val feelingDetailTextId: StateFlow<Int> = _feelingDetailTextId

    fun updateFeelingRate(value: Int) {
        if (value in (0..10)) {
            _feelingRate.value = value
            when (value) {
                in (0..2) -> {
                    _feelingStatusText.value = "Easy"
                    _feelingDetailTextId.value = R.string.easy_rpe
                }

                in (3..5) -> {
                    _feelingStatusText.value = "Moderate"
                    _feelingDetailTextId.value = R.string.moderate_rpe
                }
                in (6..9) -> {
                    _feelingStatusText.value = "Hard"
                    _feelingDetailTextId.value = R.string.hard_rpe
                }
                10 -> {
                    _feelingStatusText.value = "Max Effort"
                    _feelingDetailTextId.value = R.string.max_effort_rpe
                }
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