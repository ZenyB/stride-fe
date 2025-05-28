package com.trio.stride.ui.screens.traininglog

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.base.Resource
import com.trio.stride.data.datastoremanager.SportManager
import com.trio.stride.data.remote.dto.TrainingLogFilterDto
import com.trio.stride.domain.model.Sport
import com.trio.stride.domain.model.TrainingLogFilter
import com.trio.stride.domain.model.TrainingLogFilterDataType
import com.trio.stride.domain.model.TrainingLogItem
import com.trio.stride.domain.usecase.traininglog.GetTrainingLogsUseCase
import com.trio.stride.domain.viewstate.IViewState
import com.trio.stride.ui.utils.getEndOfWeekInMillis
import com.trio.stride.ui.utils.getStartOf12WeeksInMillis
import com.trio.stride.ui.utils.minus12Weeks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class TrainingLogViewModel @Inject constructor(
    private val getTrainingLogsUseCase: GetTrainingLogsUseCase,
    private val sportManager: SportManager,
) : BaseViewModel<TrainingLogViewModel.ViewState>() {

    override fun createInitialState(): ViewState = ViewState()

    val sports: StateFlow<List<Sport>> = sportManager.sports

    init {
        getTrainingLogsData()
        setState {
            currentState.copy(
                trainingLogFilter = currentState.trainingLogFilter.copy(
                    selectedSports = sports.value
                )
            )
        }
    }

    fun getTrainingLogsData() {
        viewModelScope.launch {
            getTrainingLogsUseCase.invoke(
                TrainingLogFilterDto()
            ).collectLatest { response ->
                when (response) {
                    is Resource.Loading -> setState { currentState.copy(isLoading = true) }
                    is Resource.Success -> {
                        setState { currentState.copy(trainingLogsData = response.data) }
                        buildWeeks(
                            currentState.currentStartDate,
                            currentState.currentEndDate,
                            currentState.trainingLogsData
                        )
                    }

                    is Resource.Error -> {
                        setState { currentState.copy(isFetchError = true) }
                    }
                }
            }
        }
    }

    private fun buildWeeks(
        startMillis: Long,
        endMillis: Long,
        rawData: List<TrainingLogItem>
    ) {
        val startDate =
            Instant.ofEpochMilli(startMillis).atZone(ZoneId.systemDefault()).toLocalDate()
        val endDate = Instant.ofEpochMilli(endMillis).atZone(ZoneId.systemDefault()).toLocalDate()

        val dataByDate = rawData.associateBy {
            Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
        }

        val result = mutableListOf<WeekInfo>()

        var current = startDate.with(DayOfWeek.MONDAY)

        while (!current.isAfter(endDate)) {
            val trainingLogItems = (0..6).map { offset ->
                val date = current.plusDays(offset.toLong())
                val data = dataByDate[date]
                data
            }

            result.add(
                WeekInfo(
                    startDate = current.atStartOfDay(ZoneId.systemDefault()).toInstant()
                        .toEpochMilli(),
                    endDate = current.plusDays(6).atStartOfDay(ZoneId.systemDefault()).toInstant()
                        .toEpochMilli(),
                    data = trainingLogItems
                )
            )

            current = current.plusWeeks(1)
        }

        result.reverse()

        Log.i("TRAINING_LOG", result.toString())

        val newWeeksInfo = currentState.weeksInfo.toMutableList()
        newWeeksInfo.addAll(result)
        filterWeeksInfo(newWeeksInfo)
        setState {
            currentState.copy(
                loadingMore = false,
                weeksInfo = newWeeksInfo,
                isLoading = false
            )
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            val filter = TrainingLogFilterDto(
                fromDate = currentState.currentStartDate.minus12Weeks(),
                toDate = currentState.currentEndDate.minus12Weeks(),
            )
            getTrainingLogsUseCase.invoke(filter).collectLatest { response ->
                when (response) {
                    is Resource.Loading -> setState { currentState.copy(loadingMore = true) }
                    is Resource.Success -> {
                        val newTrainingLogData = response.data.toMutableList()
                        newTrainingLogData.addAll(response.data)
                        setState {
                            currentState.copy(
                                trainingLogsData = newTrainingLogData,
                                currentStartDate = filter.fromDate,
                                currentEndDate = filter.toDate
                            )
                        }
                        buildWeeks(
                            filter.fromDate,
                            filter.toDate,
                            response.data
                        )
                    }

                    is Resource.Error -> {
                        setState {
                            currentState.copy(
                                isLoadMoreError = true,
                                hasLoadMorePermission = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun filterWeeksInfo(weeksInfo: List<WeekInfo>) {
        val sportIds = currentState.trainingLogFilter.selectedSports.map { it.id }

        val filteredWeeksInfo = weeksInfo.map { weekInfo ->
            weekInfo.copy(
                data = weekInfo.data.map { trainingLogItem ->
                    trainingLogItem?.let {
                        val filteredActivities = it.activities.filter { activity ->
                            sportIds.contains(activity.sport.id)
                        }
                        if (filteredActivities.isEmpty()) null
                        else it.copy(activities = filteredActivities)
                    }
                }
            )
        }

        setState { currentState.copy(currentWeeksInfo = filteredWeeksInfo) }
    }

    fun updateSportsFilter(value: List<Sport>) {
        setState {
            currentState.copy(
                trainingLogFilter = currentState.trainingLogFilter.copy(
                    selectedSports = value
                )
            )
        }
        filterWeeksInfo(currentState.weeksInfo)
    }

    fun updateDataTypesFilter(value: TrainingLogFilterDataType) {
        setState {
            currentState.copy(
                trainingLogFilter = currentState.trainingLogFilter.copy(
                    dataType = value
                )
            )
        }
        filterWeeksInfo(currentState.weeksInfo)
    }

    fun onSelectTrainingLog(trainingLog: TrainingLogItem) {
        setState { currentState.copy(selectedTrainingLog = trainingLog) }
    }

    fun deSelectTrainingLog() {
        setState { currentState.copy(selectedTrainingLog = null) }
    }

    fun resetFetchError() {
        setState { currentState.copy(isFetchError = false) }
    }

    fun resetLoadMoreErrorAndPermission() {
        setState { currentState.copy(isLoadMoreError = false, hasLoadMorePermission = true) }
    }

    fun resetLoadMoreError() {
        setState { currentState.copy(isLoadMoreError = false) }
    }

    data class ViewState(
        val isLoading: Boolean = true,
        val isFetchError: Boolean = false,
        val isLoadMoreError: Boolean = false,
        val hasLoadMorePermission: Boolean = true,
        val loadingMore: Boolean = false,
        val currentStartDate: Long = getStartOf12WeeksInMillis(),
        val currentEndDate: Long = getEndOfWeekInMillis(),
        val trainingLogsData: List<TrainingLogItem> = emptyList(),
        val trainingLogFilter: TrainingLogFilter = TrainingLogFilter(),
        val weeksInfo: List<WeekInfo> = emptyList(),
        val currentWeeksInfo: List<WeekInfo> = emptyList(),
        val selectedTrainingLog: TrainingLogItem? = null,
    ) : IViewState

    data class WeekInfo(
        val startDate: Long,
        val endDate: Long,
        val data: List<TrainingLogItem?>
    )
}