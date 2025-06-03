package com.trio.stride.domain.usecase.notification

import com.trio.stride.domain.repository.NotificationRepository
import javax.inject.Inject

class MakeSeenAllNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(): Boolean {
        return notificationRepository.makeSeenAllNoti()
    }
}