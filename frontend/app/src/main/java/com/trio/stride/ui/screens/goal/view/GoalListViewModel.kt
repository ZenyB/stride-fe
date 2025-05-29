package com.trio.stride.ui.screens.goal.view

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.base.Resource
import com.trio.stride.domain.model.GoalItem
import com.trio.stride.domain.repository.GoalRepository
import com.trio.stride.domain.usecase.goal.DeleteUserGoalUseCase
import com.trio.stride.domain.usecase.goal.GetUserGoalUseCase
import com.trio.stride.domain.viewstate.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalListViewModel @Inject constructor(
    private val getUserGoalUseCase: GetUserGoalUseCase,
    private val deleteUserGoalUseCase: DeleteUserGoalUseCase,
    private val goalRepository: GoalRepository
) : BaseViewModel<UserGoalState>() {

    var items by mutableStateOf<List<GoalItem>>(emptyList())
    var isRefreshing by mutableStateOf(false)
        private set

    var selectedItemId by mutableStateOf<String?>(null)

    init {
        getLocalGoals()
        if (!goalRepository.hasSynced.get()) {
            getRemoteUserGoals()
        }
    }

    fun refresh() {
        isRefreshing = true
        items = emptyList()
        getRemoteUserGoals()
    }

    private fun getLocalGoals() {
        viewModelScope.launch {
            Log.d("Goal", "Loading local Goal")
            setState { UserGoalState.Loading }

            goalRepository.getGoalLocal()
                .collectLatest { localData ->
                    Log.d("Goal", "Loading local Goal: ${localData}")
                    items = localData
                    setState { UserGoalState.Idle }

                }
        }
    }

    private fun getRemoteUserGoals() {
        Log.d("okhttp", "Getting user goals")
        viewModelScope.launch {
            getUserGoalUseCase()
                .collectLatest { data ->
                    Log.d("Goal", "Loading remote Goal: ${data}")

                    if (isRefreshing) {
                        when (data) {
                            is Resource.Loading -> {
                                setState { UserGoalState.Loading }
                            }

                            is Resource.Success -> {
                                isRefreshing = false
                                setState { UserGoalState.Idle }
                            }

                            is Resource.Error -> {
                                setState {
                                    UserGoalState.Error(
                                        data.error.message ?: "An error occurred"
                                    )
                                }
                            }
                        }
                    }
                }

        }
    }

    fun deleteGoal() {
        if (selectedItemId != null) {
            setState { UserGoalState.Saving }
            Log.d("okhttp", "Delete user goals")
            viewModelScope.launch {
                val result = deleteUserGoalUseCase(selectedItemId!!)

                result
                    .onSuccess { data ->
                        setState { UserGoalState.SavingSuccess }
                        Log.d("okhttp", "Delete success")
                    }
                    .onFailure {
                        setState { UserGoalState.SavingError(it.message ?: "An error occurred") }
                    }
            }
        }
    }

    fun resetState() {
        setState { UserGoalState.Idle }
    }

    override fun createInitialState(): UserGoalState {
        return UserGoalState.Idle
    }
}

sealed class UserGoalState : IViewState {
    data object Idle : UserGoalState()
    data object Loading : UserGoalState()
    data object SavingSuccess : UserGoalState()
    data class Error(val message: String) : UserGoalState()
    data class SavingError(val message: String) : UserGoalState()
    data object Saving : UserGoalState()

}