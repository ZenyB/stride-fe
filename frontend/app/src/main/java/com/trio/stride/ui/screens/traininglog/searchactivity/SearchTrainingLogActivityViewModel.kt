package com.trio.stride.ui.screens.traininglog.searchactivity

import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.base.Resource
import com.trio.stride.domain.model.ActivityFilter
import com.trio.stride.domain.model.ActivityItem
import com.trio.stride.domain.usecase.activity.FilterActivityUseCase
import com.trio.stride.domain.viewstate.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchTrainingLogActivityViewModel @Inject constructor(
    private val filterActivityUseCase: FilterActivityUseCase
) : BaseViewModel<SearchTrainingLogActivityViewModel.ViewState>() {

    private val queryFlow = MutableStateFlow("")

    override fun createInitialState(): ViewState = ViewState()

    init {
        filterActivities()
        viewModelScope.launch {
            queryFlow
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { debouncedQuery ->
                    filterActivities()
                }
        }
    }

    private fun filterActivities() {
        viewModelScope.launch {
            filterActivityUseCase.invoke(page = 1, activityFilter = currentState.filter)
                .collectLatest { response ->
                    when (response) {
                        is Resource.Loading -> {
                            setState { currentState.copy(isLoading = true) }
                        }

                        is Resource.Success -> {
                            setState {
                                currentState.copy(
                                    currentActivities = response.data.data,
                                    totalPages = response.data.page.totalPages,
                                    currentPage = currentPage + 1,
                                    isLoading = false
                                )
                            }
                        }

                        is Resource.Error -> {}
                    }
                }
        }
    }

    fun loadMore() {
        if (!currentState.isLoading && currentState.hasNextPage) {
            viewModelScope.launch {
                filterActivityUseCase.invoke(
                    page = currentState.currentPage,
                    activityFilter = currentState.filter
                ).collectLatest { response ->
                    when (response) {
                        is Resource.Loading -> {
                            setState { currentState.copy(isLoadMore = true) }
                        }

                        is Resource.Success -> {
                            val newActivities = currentState.currentActivities.toMutableList()
                            newActivities.addAll(response.data.data)
                            setState {
                                currentState.copy(
                                    currentActivities = newActivities,
                                    totalPages = response.data.page.totalPages,
                                    currentPage = currentPage + 1,
                                    isLoadMore = false,
                                    hasNextPage = currentPage < (response.data.page.totalPages
                                        ?: Int.MIN_VALUE)
                                )
                            }
                        }

                        is Resource.Error -> {
                            setState {
                                currentState.copy(
                                    isLoadMore = false,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        setState { currentState.copy(filter = currentState.filter.copy(search = newQuery)) }
        queryFlow.value = newQuery
    }

    data class ViewState(
        val isLoading: Boolean = false,
        val isLoadMore: Boolean = false,
        val hasNextPage: Boolean = true,
        val currentActivities: List<ActivityItem> = emptyList(),
        val currentPage: Int = 1,
        val totalPages: Int? = null,
        val filter: ActivityFilter = ActivityFilter()
    ) : IViewState
}