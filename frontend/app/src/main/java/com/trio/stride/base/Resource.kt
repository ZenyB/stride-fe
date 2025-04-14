package com.trio.stride.base

sealed class Resource<T> {
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val error: StrideException, data: T? = null) : Resource<T>()
    class Loading<T> : Resource<T>()
}
