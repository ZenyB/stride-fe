package com.trio.stride.ui.screens.progress

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.data.local.dao.AvailableSportDao
import com.trio.stride.data.mapper.roomdatabase.toModel
import com.trio.stride.data.mapper.roomdatabase.toSportList
import com.trio.stride.domain.model.Progress
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.repository.ProgressRepository
import com.trio.stride.domain.usecase.progress.GetProgressOverviewUseCase
import com.trio.stride.domain.viewstate.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressOverviewViewModel @Inject constructor(
    private val getProgressOverviewUseCase: GetProgressOverviewUseCase,
    private val availableSportDao: AvailableSportDao,
    private val progressRepository: ProgressRepository
) : BaseViewModel<OverviewProgressState>() {
    private var _progressData = MutableStateFlow<List<Progress>>(emptyList())
    val progressData: StateFlow<List<Progress>> = _progressData

    private var _sportList = MutableStateFlow<List<Sport>>(emptyList())
    val sportList: StateFlow<List<Sport>> = _sportList

    private var progressJob: Job? = null


    fun initData() {
        viewModelScope.launch {
            getSportId()
            getProgressOverviewUseCase().collectLatest { data ->
                Log.d("Progress", "Fetch from remote: $data")
            }
        }
    }

    fun selectSport(sportId: String) {
        if (sportId != currentState.sportId) {
            setState {
                currentState.copy(sportId = sportId)
            }
        }
    }

    fun selectIndex(index: Int?) {
        if (index != currentState.selectedIndex) {
            setState {
                currentState.copy(selectedIndex = index)
            }
        }
    }

    private fun getSportId() {
        viewModelScope.launch {
            availableSportDao.getAllSports()
                .map { it.toSportList() }
                .collectLatest { sports ->
                    _sportList.value = sports
                    if (currentState.sportId == null) {
                        val defaultSport = sports.firstOrNull()
                        setState { currentState.copy(sportId = defaultSport?.id) }
                    }
                }
        }
    }

    fun getProgress() {
        val sportId = currentState.sportId ?: return
        setState { currentState.copy(state = OverviewLoadingState.Loading) }
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            Log.d("Progress", "Loading local progress")
            progressRepository.getProgressOverviewLocal(sportId)
                .onStart {
                    setState { currentState.copy(state = OverviewLoadingState.Loading) }
                }
                .collectLatest { localData ->
                    _progressData.value = localData.map { it.toModel() }
                    setState { currentState.copy(state = OverviewLoadingState.Idle) }
                }
        }
    }

    override fun createInitialState(): OverviewProgressState {
        return OverviewProgressState()
    }
}

data class OverviewProgressState(
    val sportId: String? = null,
    val selectedIndex: Int? = null,
    val state: OverviewLoadingState = OverviewLoadingState.Idle
) : IViewState

sealed class OverviewLoadingState : IViewState {
    data object Idle : OverviewLoadingState()
    data object Loading : OverviewLoadingState()
    data class Error(val message: String) : OverviewLoadingState()
}