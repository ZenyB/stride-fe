package com.trio.stride.ui.screens.activity

import androidx.lifecycle.ViewModel
import com.trio.stride.data.repositoryimpl.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    heartRateRepo: RecordRepository
) : ViewModel() {
    val heartRate: StateFlow<Int> = heartRateRepo.heartRate

}