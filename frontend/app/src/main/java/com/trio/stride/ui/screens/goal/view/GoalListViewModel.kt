package com.trio.stride.ui.screens.goal.view

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.domain.model.GoalItem
import com.trio.stride.domain.usecase.goal.GetUserGoalUseCase
import com.trio.stride.domain.viewstate.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalListViewModel @Inject constructor(
    private val getUserGoalUseCase: GetUserGoalUseCase,
) : BaseViewModel<UserGoalState>() {

    var items by mutableStateOf<List<GoalItem>>(emptyList())
    var isRefreshing by mutableStateOf(false)
        private set

    init {
        getUserGoals()
    }

    fun refresh() {
        isRefreshing = true
        items = emptyList()
        getUserGoals()
    }

    fun getUserGoals() {
        if (currentState == UserGoalState.Loading
        ) return

        Log.d("okhttp", "Getting user goals")
        setState { UserGoalState.Loading }

        viewModelScope.launch {
            val result =
                getUserGoalUseCase()
            result
                .onSuccess { data ->
                    setState { UserGoalState.Idle }
                    if (data != null) {
                        items = data.data
                    }
                    isRefreshing = false
                }
                .onFailure {
                    setState { UserGoalState.Error(it.message ?: "An error occurred") }
                }
        }
    }

    override fun createInitialState(): UserGoalState {
        return UserGoalState.Idle
    }
}

sealed class UserGoalState : IViewState {
    data object Idle : UserGoalState()
    data object Loading : UserGoalState()
    data object Refreshing : UserGoalState()
    data class Error(val message: String) : UserGoalState()
}