package com.trio.stride.domain.model

data class UserData(
    val userId: String?,
    val email: String?,
    val name: String?,
    val profilePictureUrl: String?,
    val idToken: String?
)

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)