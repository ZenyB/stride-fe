package com.trio.stride.ui.screens.notification

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.trio.stride.base.BaseViewModel
import com.trio.stride.base.Resource
import com.trio.stride.domain.model.NotificationItem
import com.trio.stride.domain.model.UserInfo
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
    private val makeSeenAllNotificationsUseCase: MakeSeenAllNotificationsUseCase
) : BaseViewModel<NotificationViewModel.ViewState>() {

    override fun createInitialState(): ViewState = ViewState()

    init {
        viewModelScope.launch {
            getUserUseCase.invoke().collectLatest { user ->
                if (user is Resource.Success) {
                    setState { currentState.copy(user = user.data) }
                }
            }
            getNotifications()
        }
    }

    private fun getNotifications() {
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
                        val notis = currentState.notifications.toMutableList()
                        notis.addAll(response.data)
                        val hasMore = response.data.isNotEmpty()
                        setState {
                            currentState.copy(
                                notifications = notis,
                                isLoading = false,
                                hasNextPage = hasMore
                            )
                        }
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

    fun loadMore() {
        viewModelScope.launch {
            val newPage = currentState.currentPage + 1
            getNotificationsUseCase.invoke(newPage).collectLatest { response ->
                when (response) {
                    is Resource.Loading -> setState {
                        currentState.copy(
                            loadingMore = true, loadMoreError = false
                        )
                    }

                    is Resource.Success -> {
                        if (response.data.isEmpty()) {
                            setState { currentState.copy(loadingMore = false, hasNextPage = false) }
                        } else {
                            val notis = currentState.notifications.toMutableList()
                            notis.addAll(response.data)
                            setState {
                                currentState.copy(
                                    notifications = notis,
                                    loadingMore = false
                                )
                            }
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
        val loadingMore: Boolean = false,
        val loadMoreError: Boolean = false,
        val isError: Boolean = false,
        val errorMessage: String? = null,
        val hasNextPage: Boolean = true,
        val notifications: List<NotificationItem> = emptyList(),
        val user: UserInfo = UserInfo(),
        val currentPage: Int = 1,
    ) : IViewState
}