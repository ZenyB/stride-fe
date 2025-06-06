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
import com.trio.stride.domain.model.TrainingLogMetaData
import com.trio.stride.domain.usecase.traininglog.GetTrainingLogsUseCase
import com.trio.stride.domain.viewstate.IViewState
import com.trio.stride.ui.utils.getEndOfWeekInMillis
import com.trio.stride.ui.utils.getStartOf12WeeksInMillis
import com.trio.stride.ui.utils.getStartOfWeekInMillis
import com.trio.stride.ui.utils.getTodayInMillis
import com.trio.stride.ui.utils.minus12Weeks
import com.trio.stride.ui.utils.systemZoneId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.YearMonth
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class TrainingLogViewModel @Inject constructor(
    private val getTrainingLogsUseCase: GetTrainingLogsUseCase,
    private val sportManager: SportManager,
) : BaseViewModel<TrainingLogViewModel.ViewState>() {

    override fun createInitialState(): ViewState = ViewState()
    private val startDateOf12Weeks = getStartOf12WeeksInMillis()

    val sports: StateFlow<List<Sport>> = sportManager.sports

    init {
        getFirstTrainingLogsData()
        setState {
            currentState.copy(
                trainingLogFilter = currentState.trainingLogFilter.copy(
                    selectedSports = sports.value
                )
            )
        }
    }

    private fun getFirstTrainingLogsData() {
        viewModelScope.launch {
            val today = getTodayInMillis()
            getTrainingLogsUseCase.invoke(
                TrainingLogFilterDto(
                    fromDate = today,
                    toDate = today
                )
            ).collectLatest { response ->
                when (response) {
                    is Resource.Loading -> setState { currentState.copy(isLoading = true) }
                    is Resource.Success -> {
                        val startOfMetadata =
                            getStartOfWeekInMillis(response.data.metaData.from)

                        setState {
                            currentState.copy(
                                metaData = response.data.metaData,
                                nextStartDate = maxOf(
                                    startOfMetadata,
                                    startDateOf12Weeks
                                ),
                            )
                        }

                        if (startDateOf12Weeks < startOfMetadata) {
                            setState { currentState.copy(hasLoadMorePermission = false) }
                        }

                        getTrainingLogsData()
                    }

                    is Resource.Error -> {
                        setState { currentState.copy(isFetchError = true) }
                        Log.i("Error", response.error.toString())
                    }
                }
            }
        }
    }

    fun getTrainingLogsData() {
        viewModelScope.launch {
            val filter = TrainingLogFilterDto(
                fromDate = currentState.nextStartDate,
                toDate = currentState.nextEndDate
            )
            getTrainingLogsUseCase.invoke(filter).collectLatest { response ->
                when (response) {
                    is Resource.Loading -> setState { currentState.copy(isLoading = true) }
                    is Resource.Success -> {
                        val startDateOf12WeeksBefore = currentState.nextStartDate.minus12Weeks()
                        val startDateOfMetaData =
                            getStartOfWeekInMillis(response.data.metaData.from)

                        buildWeeks(
                            filter.fromDate,
                            filter.toDate,
                            response.data.trainingLogs
                        )

                        when {
                            currentState.nextStartDate == startDateOfMetaData -> setState {
                                currentState.copy(
                                    hasLoadMorePermission = false
                                )
                            }

                            startDateOf12WeeksBefore <= startDateOfMetaData -> setState {
                                currentState.copy(
                                    nextEndDate = nextStartDate - 1,
                                    nextStartDate = startDateOfMetaData,
                                )
                            }

                            startDateOf12WeeksBefore > startDateOfMetaData -> setState {
                                currentState.copy(
                                    nextStartDate = startDateOf12WeeksBefore,
                                    nextEndDate = nextEndDate.minus12Weeks()
                                )
                            }
                        }
                    }

                    is Resource.Error -> {
                        setState { currentState.copy(isFetchError = true) }
                        Log.i("Error", response.error.toString())
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
            Instant.ofEpochMilli(startMillis).atZone(systemZoneId).toLocalDate()
        val endDate = Instant.ofEpochMilli(endMillis).atZone(systemZoneId).toLocalDate()

        val dataByDate = rawData.associateBy {
            Instant.ofEpochMilli(it.date).atZone(systemZoneId).toLocalDate()
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
                    startDate = current.atStartOfDay(systemZoneId).toInstant()
                        .toEpochMilli(),
                    endDate = current.plusDays(6).atStartOfDay(systemZoneId).toInstant()
                        .toEpochMilli(),
                    data = trainingLogItems
                )
            )

            current = current.plusWeeks(1)
        }

        result.reverse()

        val newWeeksInfo = currentState.weeksInfo.toMutableList()
        newWeeksInfo.addAll(result)
        filterWeeksInfo(newWeeksInfo)
        setState {
            currentState.copy(
                trainingLogsData = trainingLogsData,
                loadingMore = false,
                weeksInfo = newWeeksInfo,
                isLoading = false
            )
        }
    }

    fun loadMore(successCallBack: () -> Unit = {}) {
        Log.i("LOAD_MORE", currentState.hasLoadMorePermission.toString())
        if (currentState.hasLoadMorePermission) {
            viewModelScope.launch {
                val filter = TrainingLogFilterDto(
                    fromDate = currentState.nextStartDate,
                    toDate = currentState.nextEndDate
                )

                if (filter.fromDate > filter.toDate) {
                    setState { currentState.copy(hasLoadMorePermission = false) }
                } else {
                    getTrainingLogsUseCase.invoke(filter).collectLatest { response ->
                        when (response) {
                            is Resource.Loading -> setState { currentState.copy(loadingMore = true) }
                            is Resource.Success -> {
                                val newTrainingLogData = response.data.trainingLogs.toMutableList()
                                newTrainingLogData.addAll(response.data.trainingLogs)

                                buildWeeks(
                                    filter.fromDate,
                                    filter.toDate,
                                    response.data.trainingLogs
                                )

                                val startDateOf12WeeksBefore =
                                    currentState.nextStartDate.minus12Weeks()
                                val startDateOfMetaData =
                                    getStartOfWeekInMillis(response.data.metaData.from)

                                when {
                                    currentState.nextStartDate == startDateOfMetaData -> setState {
                                        currentState.copy(
                                            hasLoadMorePermission = false
                                        )
                                    }

                                    startDateOf12WeeksBefore <= startDateOfMetaData -> setState {
                                        currentState.copy(
                                            nextEndDate = nextStartDate - 1,
                                            nextStartDate = startDateOfMetaData
                                        )
                                    }

                                    startDateOf12WeeksBefore > startDateOfMetaData -> setState {
                                        currentState.copy(
                                            nextStartDate = startDateOf12WeeksBefore,
                                            nextEndDate = nextEndDate.minus12Weeks()
                                        )
                                    }
                                }
                                successCallBack()
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

    private fun isMonthDataLoaded(selectedMonth: YearMonth): Boolean {

        return currentState.weeksInfo.any { week ->
            val weekStart = Instant.ofEpochMilli(week.startDate)
                .atZone(systemZoneId)
                .toLocalDate()
            YearMonth.from(weekStart) == selectedMonth
        }
    }

    private suspend fun loadMoreAsync() = suspendCoroutine<Unit> { continuation ->
        loadMore {
            continuation.resume(Unit)
        }
    }

    suspend fun scrollToTargetMonth(targetMonth: YearMonth): Int {
        while (!isMonthDataLoaded(targetMonth)) {
            setState { currentState.copy(scrolling = true) }
            loadMoreAsync()
        }

        setState { currentState.copy(scrolling = false) }

        return currentState.weeksInfo.indexOfFirst { week ->
            val weekStart = Instant.ofEpochMilli(week.startDate)
                .atZone(systemZoneId)
                .toLocalDate()
            YearMonth.from(weekStart) == targetMonth
        }
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
        val scrolling: Boolean = false,
        val nextStartDate: Long = getStartOfWeekInMillis(),
        val nextEndDate: Long = getEndOfWeekInMillis(),
        val trainingLogsData: List<TrainingLogItem> = emptyList(),
        val trainingLogFilter: TrainingLogFilter = TrainingLogFilter(),
        val weeksInfo: List<WeekInfo> = emptyList(),
        val currentWeeksInfo: List<WeekInfo> = emptyList(),
        val selectedTrainingLog: TrainingLogItem? = null,
        val metaData: TrainingLogMetaData = TrainingLogMetaData(
            from = getStartOf12WeeksInMillis(),
            to = getEndOfWeekInMillis()
        ),
    ) : IViewState

    data class WeekInfo(
        val startDate: Long,
        val endDate: Long,
        val data: List<TrainingLogItem?>
    )
}