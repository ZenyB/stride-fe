package com.trio.stride.domain.usecase.profile

import com.trio.stride.data.mapper.roomdatabase.toCurrentUserEntity
import com.trio.stride.domain.repository.UserRepository
import javax.inject.Inject

class SyncUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke() {
        val remoteData = repository.getUser()
        repository.saveCurrentUser(remoteData.toCurrentUserEntity())
    }
}