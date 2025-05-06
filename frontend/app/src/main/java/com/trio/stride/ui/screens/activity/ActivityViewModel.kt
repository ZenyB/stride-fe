package com.trio.stride.ui.screens.activity

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.domain.model.ActivityItem
import com.trio.stride.domain.usecase.activity.GetAllActivityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val getAllActivityUseCase: GetAllActivityUseCase,
) : BaseViewModel<ActivityListState>() {

    var items by mutableStateOf<List<ActivityItem>>(emptyList())
    var isRefreshing by mutableStateOf(false)
        private set

    private var currentPage = 1
    private val limit = 10
    private var totalPages: Int? = null

    init {
        getAllActivity()
    }

    fun refresh() {
        isRefreshing = true
        currentPage = 1
        totalPages = null
        items = emptyList()
        getAllActivity()
    }

    fun getAllActivity() {
        if (currentState == ActivityListState.Loading
            || (totalPages != null && currentPage > totalPages!!)
        ) return

        Log.d("okhttp", "Getting activities")
        setState { ActivityListState.Loading }

        viewModelScope.launch {
            val result =
                getAllActivityUseCase(
                    currentPage,
                    limit
                )
            result
                .onSuccess { data ->
                    setState { ActivityListState.Idle }
                    if (data != null) {
                        items = items + data.data
                        totalPages = data.page.totalPages
                        currentPage++
                    }
                    isRefreshing = false
                }
                .onFailure {
                    setState { ActivityListState.Error(it.message ?: "An error occurred") }
                }
        }
    }

    override fun createInitialState(): ActivityListState {
        return ActivityListState.Idle
    }

}