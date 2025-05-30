package com.trio.stride.ui.screens.activity

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.base.Resource
import com.trio.stride.data.datastoremanager.SportManager
import com.trio.stride.domain.model.ActivityItem
import com.trio.stride.domain.repository.ActivityRepository
import com.trio.stride.domain.usecase.activity.GetAllActivityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val getAllActivityUseCase: GetAllActivityUseCase,
    private val activityRepository: ActivityRepository,
    private val sportManager: SportManager,
) : BaseViewModel<ActivityListState>() {

    var items by mutableStateOf<List<ActivityItem>>(emptyList())
    var isRefreshing by mutableStateOf(false)
        private set

    private var currentPage = 1
    private val limit = 10
    private var totalPages: Int? = null

    init {
        viewModelScope.launch {
            while (sportManager.sports.value.isEmpty()) {
                delay(100L)
            }

            activityRepository.getRecentLocalActivity().collectLatest { data ->
                if (items.isEmpty()) {
                    items = data
                }
            }
        }
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
            getAllActivityUseCase(
                currentPage,
                limit
            )
                .collectLatest { response ->
                    when (response) {
                        is Resource.Success -> {
                            setState { ActivityListState.Idle }
                            if (response.data != null) {
                                if (currentPage == 1) {
                                    items = emptyList()
                                }
                                items = items + response.data.data
                                totalPages = response.data.page.totalPages
                                currentPage++
                            }
                            isRefreshing = false
                        }

                        is Resource.Error -> setState {
                            ActivityListState.Error(
                                response.error.message ?: "An error occurred"
                            )
                        }

                        else -> Unit
                    }

                }

        }
    }

    fun getRefreshActivity() {
        Log.d("okhttp", "Re fetch Getting activities")
        setState { ActivityListState.Loading }

        viewModelScope.launch {
            getAllActivityUseCase(
                1,
                currentPage * limit
            )
                .collectLatest { response ->
                    when (response) {
                        is Resource.Success -> {
                            setState { ActivityListState.Idle }
                            if (response.data != null) {
                                items = response.data.data
                            }
                            isRefreshing = false
                        }

                        is Resource.Error -> setState {
                            ActivityListState.Error(
                                response.error.message ?: "An error occurred"
                            )
                        }

                        else -> Unit
                    }

                }

        }
    }

    override fun createInitialState(): ActivityListState {
        return ActivityListState.Idle
    }

}