package com.trio.stride.domain.usecase.profile

import com.trio.stride.domain.repository.UserRepository
import javax.inject.Inject

class ClearLocalUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        userRepository.clearCurrentUser()
    }
}