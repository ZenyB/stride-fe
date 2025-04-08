package com.trio.stride.data.repositoryimpl

import com.trio.stride.data.apiservice.user.UserApi
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
            name = response.name,
            ava = response.ava,
            dob = response.dob,
            height = response.height,
            weight = response.weight,
            male = response.male,
            city = response.city,
            maxHeartRate = response.maxHeartRate,
            equipmentsWeight = EquipmentsWeight(
                shoes = response.equipmentsWeight.SHOES,
                bag = response.equipmentsWeight.BAG
            ),
            heartRateZones = HeartRateZones(
                zone1 = response.heartRateZones.ZONE1,
                zone2 = response.heartRateZones.ZONE2,
                zone3 = response.heartRateZones.ZONE3,
                zone4 = response.heartRateZones.ZONE4,
                zone5 = response.heartRateZones.ZONE5
            ),
            isBlock = response.isBlock
        )
    }
}