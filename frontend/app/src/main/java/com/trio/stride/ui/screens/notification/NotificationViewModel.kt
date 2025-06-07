package com.trio.stride.ui.screens.notification

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.base.Resource
import com.trio.stride.domain.model.NotificationItem
import com.trio.stride.domain.model.UserInfo
import com.trio.stride.domain.repository.NotificationRepository
import com.trio.stride.domain.usecase.notification.GetNotificationsUseCase
import com.trio.stride.domain.usecase.notification.MakeSeenAllNotificationsUseCase
import com.trio.stride.domain.usecase.notification.MakeSeenNotificationUseCase
import com.trio.stride.domain.usecase.profile.GetUserUseCase
import com.trio.stride.domain.viewstate.IViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val makeSeenNotificationUseCase: MakeSeenNotificationUseCase,
    private val makeSeenAllNotificationsUseCase: MakeSeenAllNotificationsUseCase,
    private val notificationRepository: NotificationRepository
) : BaseViewModel<NotificationViewModel.ViewState>() {

    override fun createInitialState(): ViewState = ViewState()

    var isInitialLoadDone = false

    init {
        viewModelScope.launch {
            setState { currentState.copy(isLoading = true) }
            getUserUseCase.invoke().collectLatest { user ->
                if (user is Resource.Success) {
                    setState { currentState.copy(user = user.data) }
                }
            }
            getLocalNotifications()
            refreshNotifications()
        }
    }

    private fun getLocalNotifications() {
        viewModelScope.launch {
            val items = notificationRepository.lcGetNotificationsPage1()
            Log.i("LOCAL_NOTIFICATON", items.toString())
            isInitialLoadDone = true
            setState { currentState.copy(isLoading = false, notifications = items) }
        }
    }

    private fun initNotifications() {
        viewModelScope.launch {
            getNotificationsUseCase.invoke().collectLatest { response ->
                when (response) {
                    is Resource.Loading -> setState {
                        currentState.copy(
                            isLoading = true, isError = false, errorMessage = null
                        )
                    }

                    is Resource.Success -> {
                        Log.i("NOTIFICATION_GET", response.data.toString())
                        setState {
                            currentState.copy(
                                notifications = response.data.notificationItems,
                                isLoading = false,
                                hasNextPage = true,
                            )
                        }
                        isInitialLoadDone = true
                        refreshNotifications()
                    }

                    is Resource.Error -> {
                        Log.i("GET_NOTI_ERR", response.error.message.toString())
                        setState {
                            currentState.copy(
                                isError = true,
                                errorMessage = response.error.message.toString(),
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun refreshNotifications() {
        viewModelScope.launch {
            getNotificationsUseCase.invoke(page = 1, forceRefresh = true)
                .collectLatest { response ->
                    when (response) {
                        is Resource.Loading -> setState { currentState.copy(isRefresh = true) }

                        is Resource.Success -> {
                            setState {
                                currentState.copy(
                                    notifications = response.data.notificationItems,
                                    hasNextPage = response.data.page.index < (response.data.page.totalPages
                                        ?: Int.MAX_VALUE),
                                    totalPages = response.data.page.totalPages,
                                    currentPage = response.data.page.index,
                                    isRefresh = false,
                                    isError = false,
                                    errorMessage = null
                                )
                            }
                            Log.i("REFRESH", response.data.notificationItems.toString())
                        }

                        is Resource.Error -> {
                            Log.i("GET_NOTI_ERR", response.error.message.toString())
                            setState {
                                currentState.copy(
                                    isError = true,
                                    errorMessage = response.error.message.toString(),
                                    isRefresh = false
                                )
                            }
                        }
                    }
                }
        }
    }

    fun loadMore() {
        if (!isInitialLoadDone || currentState.isLoading || currentState.loadingMore || currentState.isRefresh
            || !currentState.hasNextPage || currentState.totalPages == null
        ) return
        Log.i("LOAD_MORE", currentState.notifications.toString())
        viewModelScope.launch {
            val newPage = currentState.currentPage + 1
            getNotificationsUseCase.invoke(newPage, forceRefresh = true).collectLatest { response ->
                when (response) {
                    is Resource.Loading -> setState {
                        currentState.copy(
                            loadingMore = true, loadMoreError = false
                        )
                    }

                    is Resource.Success -> {
                        val notis = currentState.notifications.toMutableList()
                        Log.i("LOAD_MORE_SUCCESS", response.data.notificationItems.toString())
                        notis.addAll(response.data.notificationItems)
                        setState {
                            currentState.copy(
                                notifications = notis,
                                loadingMore = false,
                                currentPage = response.data.page.index,
                                hasNextPage = newPage < (response.data.page.totalPages
                                    ?: Int.MAX_VALUE),
                                totalPages = response.data.page.totalPages
                            )
                        }
                    }

                    is Resource.Error -> {
                        Log.i("LOAD_MORE_NOTI_ERR", response.error.message.toString())
                        setState {
                            currentState.copy(
                                loadMoreError = true,
                                loadingMore = false
                            )
                        }
                    }
                }
            }
        }
    }

    fun makeSeen(notificationId: String) {
        viewModelScope.launch {
            makeSeenNotificationUseCase.invoke(notificationId)
            val updatedNotifications = currentState.notifications.map {
                if (it.id == notificationId) it.copy(seen = true) else it
            }
            setState { currentState.copy(notifications = updatedNotifications) }
        }
    }

    fun makeSeenAll() {
        viewModelScope.launch {
            makeSeenAllNotificationsUseCase.invoke()
            val updatedNotifications = currentState.notifications.map {
                it.copy(seen = true)
            }
            setState { currentState.copy(notifications = updatedNotifications) }
        }
    }

    data class ViewState(
        val isLoading: Boolean = false,
        val isRefresh: Boolean = false,
        val loadingMore: Boolean = false,
        val loadMoreError: Boolean = false,
        val isError: Boolean = false,
        val errorMessage: String? = null,
        val hasNextPage: Boolean = true,
        val notifications: List<NotificationItem> = emptyList(),
        val user: UserInfo = UserInfo(),
        val currentPage: Int = 1,
        val totalPages: Int? = null,
    ) : IViewState
}