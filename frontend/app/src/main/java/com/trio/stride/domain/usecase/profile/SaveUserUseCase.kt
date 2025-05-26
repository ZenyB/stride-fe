package com.trio.stride.domain.usecase.profile

import com.trio.stride.data.local.dao.CurrentUserDao
import com.trio.stride.data.mapper.roomdatabase.toCurrentUserEntity
import com.trio.stride.domain.model.UserInfo
import javax.inject.Inject

class SaveUserUseCase @Inject constructor(
    private val userDao: CurrentUserDao
) {
    suspend operator fun invoke(user: UserInfo) {
        userDao.saveCurrentUser(user.toCurrentUserEntity())
    }
}