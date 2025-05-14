package com.trio.stride.data.mapper.roomdatabase

import com.trio.stride.data.local.entity.CurrentUserEntity
import com.trio.stride.domain.model.UserInfo

fun UserInfo.toCurrentUserEntity() = CurrentUserEntity(
    id = this.id,
    name = this.name,
    ava = this.ava,
    dob = this.dob,
    height = this.height,
    weight = this.weight,
    male = this.male,
    city = this.city,
    maxHeartRate = this.maxHeartRate,
    equipmentsWeight = this.equipmentsWeight,
    heartRateZones = this.heartRateZones,
    isBlock = this.isBlock
)

fun CurrentUserEntity.toUserInfo() = UserInfo(
    id = this.id,
    name = this.name,
    ava = this.ava,
    dob = this.dob,
    height = this.height,
    weight = this.weight,
    male = this.male,
    city = this.city,
    maxHeartRate = this.maxHeartRate,
    equipmentsWeight = this.equipmentsWeight,
    heartRateZones = this.heartRateZones,
    isBlock = this.isBlock
)