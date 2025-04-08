package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.apiservice.user.UserApi
import com.trio.stride.di.Authorized
import com.trio.stride.domain.model.UserInfo
import com.trio.stride.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    @Authorized val api: UserApi
) : UserRepository {

    override suspend fun getUser(): UserInfo {
        val response = api.getUser()
        return UserInfo(
            id = response.id,
            name = response.name,
            ava = response.ava,
            dob = response.dob,
            height = response.height,
            weight = response.weight,
            male = response.male,
            city = response.city,
            maxHeartRate = response.maxHeartRate,
            equipmentsWeight = response.equipmentsWeight,
            heartRateZones = response.heartRateZones,
            isBlock = response.isBlock
        )
    }
}