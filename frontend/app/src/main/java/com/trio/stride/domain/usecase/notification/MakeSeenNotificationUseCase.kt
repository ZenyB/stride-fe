package com.trio.stride.domain.usecase.notification

import com.trio.stride.domain.repository.NotificationRepository
import javax.inject.Inject

class MakeSeenNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(id: String): Boolean {
        return notificationRepository.makeSeenNoti(id)
    }
}