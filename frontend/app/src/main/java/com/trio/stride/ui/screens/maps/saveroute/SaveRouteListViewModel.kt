package com.trio.stride.ui.screens.maps.saveroute

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.data.remote.dto.UserRouteRequest
import com.trio.stride.domain.model.RouteItem
import com.trio.stride.domain.usecase.route.GetUserRouteUseCase
import com.trio.stride.domain.viewstate.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaveRouteListViewModel @Inject constructor(
    private val getUserRouteUseCase: GetUserRouteUseCase,
) : BaseViewModel<SaveRouteListState>() {

    var items by mutableStateOf<List<RouteItem>>(emptyList())
    var isRefreshing by mutableStateOf(false)
        private set

    private var currentPage = 1
    private val limit = 10
    private var totalPages: Int? = null

    init {
        getUserRoutes()
    }

    fun refresh() {
        isRefreshing = true
        currentPage = 1
        totalPages = null
        items = emptyList()
        getUserRoutes()
    }

    fun getUserRoutes() {
        if (currentState == SaveRouteListState.Loading
            || (totalPages != null && currentPage > totalPages!!)
        ) return

        Log.d("okhttp", "Getting user routes")
        setState { SaveRouteListState.Loading }

        viewModelScope.launch {
            val result =
                getUserRouteUseCase(
                    UserRouteRequest(
                        currentPage,
                        limit,
                        null
                    )
                )
            result
                .onSuccess { data ->
                    setState { SaveRouteListState.Idle }
                    if (data != null) {
                        items = items + data.data
                        totalPages = data.page.totalPages
                        currentPage++
                    }
                    isRefreshing = false
                }
                .onFailure {
                    setState { SaveRouteListState.Error(it.message ?: "An error occurred") }
                }
        }
    }

    override fun createInitialState(): SaveRouteListState {
        return SaveRouteListState.Idle
    }
}

sealed class SaveRouteListState : IViewState {
    data object Idle : SaveRouteListState()
    data object Loading : SaveRouteListState()
    data object Refreshing : SaveRouteListState()
    data class Error(val message: String) : SaveRouteListState()
}