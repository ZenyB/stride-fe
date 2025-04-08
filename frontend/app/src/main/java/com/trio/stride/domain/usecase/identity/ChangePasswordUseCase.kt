package com.trio.stride.domain.usecase.identity

import com.trio.stride.domain.repository.IdentityRepository
import com.trio.stride.ui.utils.parseErrorResponse
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val repository: IdentityRepository
) {
    suspend operator fun invoke(
        resetPasswordTokenId: String,
        otpCode: String,
        password: String
    ): Result<String> {
        return try {
            val response = repository.changePassword(resetPasswordTokenId, otpCode, password)
            if (response.isSuccessful) {
                val message =
                    if (response.body()?.data == true) "Password Changed" else "Fail To Change Password"
                Result.success(message)
            } else {
                val errorResponse = parseErrorResponse(response.errorBody())
                Result.failure(Exception(errorResponse.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}