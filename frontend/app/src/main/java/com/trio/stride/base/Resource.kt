package com.trio.stride.base

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(val error: StrideException, data: T? = null) : Resource<T>(data)
    class Loading<T> : Resource<T>()
}
