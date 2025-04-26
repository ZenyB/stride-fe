package com.trio.stride.ui.components.sport.bottomsheet

import androidx.lifecycle.ViewModel
import com.trio.stride.data.datastoremanager.SportManager
import com.trio.stride.domain.model.Category
import com.trio.stride.domain.model.Sport
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SportBottomSheetState @Inject constructor(
    private val sportManager: SportManager
) : ViewModel() {
    val isError: StateFlow<Boolean> = sportManager.isError
    val errorMessage: StateFlow<String?> = sportManager.errorMessage
    val categories: StateFlow<List<Category>> = sportManager.categories
    val sportsByCategory: StateFlow<Map<String, List<Sport>>> =
        sportManager.sportsByCategory

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet

    fun show() {
        _showBottomSheet.value = true
    }

    fun hide() {
        _showBottomSheet.value = false
    }
}