package com.trio.stride.ui.screens.progress.detail

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.base.Resource
import com.trio.stride.data.local.dao.AvailableSportDao
import com.trio.stride.data.mapper.roomdatabase.toSportList
import com.trio.stride.domain.model.Progress
import com.trio.stride.domain.model.ProgressDetails
import com.trio.stride.domain.model.ProgressTimeRange
import com.trio.stride.domain.model.ProgressType
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.model.SportMapType
import com.trio.stride.domain.usecase.progress.GetProgressDetailUseCase
import com.trio.stride.domain.viewstate.IViewState
import com.trio.stride.ui.utils.formatDistance
import com.trio.stride.ui.utils.formatDuration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressDetailViewModel @Inject constructor(
    private val getProgressDetailUseCase: GetProgressDetailUseCase,
    private val availableSportDao: AvailableSportDao
) : BaseViewModel<DetailProgressState>() {
    private var _sportList = MutableStateFlow<List<Sport>>(emptyList())
    val sportList: StateFlow<List<Sport>> = _sportList

    private var _progressData = MutableStateFlow<ProgressDetails?>(null)
    val progressData: StateFlow<ProgressDetails?> = _progressData

    private var progressJob: Job? = null

    fun initData() {
        viewModelScope.launch {
            getSportId()
            getProgressDetail()
        }
    }


    fun selectIndex(index: Int?) {
        if (index != currentState.selectedIndex) {
            setState {
                currentState.copy(selectedIndex = index)
            }
        }
    }

    fun getSelectedValue(filterType: ProgressType, selectedItem: Progress): String {
        val res = when (filterType) {
            ProgressType.DISTANCE -> "${formatDistance(selectedItem.distance)} km"
            ProgressType.ELEVATION -> "${selectedItem.elevation} m"
            ProgressType.TIME -> formatDuration(selectedItem.time)
            ProgressType.ACTIVITY -> selectedItem.numberActivities.toString()
        }
        return res
    }

    fun selectSport(selectedSport: Sport) {
        if (selectedSport.id != currentState.sport?.id) {
            if (selectedSport.sportMapType == SportMapType.NO_MAP && currentState.selectedFilterType in listOf(
                    ProgressType.DISTANCE,
                    ProgressType.ELEVATION
                )
            ) {
                setState {
                    currentState.copy(sport = selectedSport, selectedFilterType = ProgressType.TIME)
                }
            } else {
                setState {
                    currentState.copy(sport = selectedSport)
                }
            }

        }
    }

    fun selectTimeRange(selectedTimeFrame: ProgressTimeRange) {
        if (selectedTimeFrame != currentState.selectedTimeFrame) {
            setState {
                currentState.copy(selectedTimeFrame = selectedTimeFrame)
            }
        }
    }

    fun onTypeSelected(type: ProgressType) {
        if (type != uiState.value.selectedFilterType) {
            setState {
                currentState.copy(
                    selectedFilterType = type,
                )
            }
        }
    }

    private fun getSportId() {
        viewModelScope.launch {
            availableSportDao.getAllSports()
                .map { it.toSportList() }
                .collectLatest { sports ->
                    _sportList.value = sports
                    if (currentState.sport == null) {
                        val defaultSport = sports.firstOrNull()
                        if (defaultSport != null) {
                            selectSport(defaultSport)
                        }
                    }
                }
        }
    }

    fun getProgressDetail() {
        val sportId = currentState.sport?.id
        sportId ?: return
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            Log.d("Progress", "Loading local progress")
            getProgressDetailUseCase(sportId)
                .collectLatest { res ->
                    when (res) {
                        is Resource.Loading -> {
                            setState { currentState.copy(state = LoadingState.Loading) }
                        }

                        is Resource.Success -> {
                            _progressData.value = res.data
                            setState { currentState.copy(state = LoadingState.Idle) }
                        }

                        is Resource.Error -> {
                            setState {
                                currentState.copy(
                                    state = LoadingState.Error(
                                        res.error.message ?: "An error occurred"
                                    )
                                )
                            }
                        }
                    }
                }
        }
    }

    override fun createInitialState(): DetailProgressState {
        return DetailProgressState()
    }
}

data class DetailProgressState(
    val sport: Sport? = null,
    val selectedFilterType: ProgressType = ProgressType.DISTANCE,
    val selectedTimeFrame: ProgressTimeRange = ProgressTimeRange.LAST_3_MONTHS,
    val selectedIndex: Int? = null,
    val state: LoadingState = LoadingState.Idle
) : IViewState

sealed class LoadingState : IViewState {
    data object Idle : LoadingState()
    data object Loading : LoadingState()
    data class Error(val message: String) : LoadingState()
}