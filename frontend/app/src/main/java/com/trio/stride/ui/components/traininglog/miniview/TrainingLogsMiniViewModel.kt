package com.trio.stride.ui.components.traininglog.miniview

import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.base.Resource
import com.trio.stride.data.remote.dto.TrainingLogFilterDto
import com.trio.stride.domain.model.TrainingLogItem
import com.trio.stride.domain.usecase.traininglog.GetTrainingLogsUseCase
import com.trio.stride.domain.viewstate.IViewState
import com.trio.stride.ui.utils.getEndOfWeekInMillis
import com.trio.stride.ui.utils.getStartOfWeekInMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private val startDate = getStartOfWeekInMillis()
private val endDate = getEndOfWeekInMillis()

@HiltViewModel
class TrainingLogsMiniViewModel @Inject constructor(
    private val getTrainingLogsUseCase: GetTrainingLogsUseCase
) : BaseViewModel<TrainingLogsMiniViewModel.ViewState>() {

    override fun createInitialState(): ViewState = ViewState()

    init {
        getTrainingLog()
    }

    private fun getTrainingLog() {
        viewModelScope.launch {
            getTrainingLogsUseCase.invoke(
                filter = TrainingLogFilterDto(
                    fromDate = startDate,
                    toDate = endDate
                )
            ).collectLatest { response ->
                when (response) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        val trainingLogs = response.data.trainingLogs
                        val newTrainingLogs = mutableListOf<TrainingLogItem?>()

                        var currentDayMillis = startDate

                        var trainingLogsIdx = 0

                        repeat(7) {
                            if (trainingLogsIdx < trainingLogs.size
                                && trainingLogs[trainingLogsIdx].date == currentDayMillis
                            ) {
                                newTrainingLogs.add(trainingLogs[trainingLogsIdx])
                                trainingLogsIdx += 1
                            } else {
                                newTrainingLogs.add(null)
                            }

                            currentDayMillis += 24 * 60 * 60 * 1000
                        }

                        setState { currentState.copy(trainingLogs = newTrainingLogs) }
                    }

                    is Resource.Error -> {}
                }
            }
        }
    }

    data class ViewState(
        val trainingLogs: List<TrainingLogItem?> = emptyList()
    ) : IViewState
}