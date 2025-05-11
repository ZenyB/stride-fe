package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.remote.apiservice.user.UserApi
import com.trio.stride.data.remote.dto.UpdateUserRequestDto
import com.trio.stride.di.Authorized
import com.trio.stride.domain.model.EquipmentsWeight
import com.trio.stride.domain.model.HeartRateZones
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
            name = response.name ?: "",
            ava = response.ava ?: "",
            dob = response.dob ?: "",
            height = response.height ?: 0,
            weight = response.weight ?: 0,
            male = response.male == true,
            city = response.city ?: "",
            maxHeartRate = response.maxHeartRate ?: 0,
            equipmentsWeight = EquipmentsWeight(
                shoes = response.equipmentsWeight?.SHOES ?: 0,
                bag = response.equipmentsWeight?.BAG ?: 0
            ),
            heartRateZones = HeartRateZones(
                zone1 = response.heartRateZones?.ZONE1 ?: 0,
                zone2 = response.heartRateZones?.ZONE2 ?: 0,
                zone3 = response.heartRateZones?.ZONE3 ?: 0,
                zone4 = response.heartRateZones?.ZONE4 ?: 0,
                zone5 = response.heartRateZones?.ZONE5 ?: 0
            ),
            isBlock = response.isBlock == true
        )
    }

    override suspend fun updateUser(requestDto: UpdateUserRequestDto): Boolean {
        val result = api.updateUser(requestDto)
        return result.data
    }
}