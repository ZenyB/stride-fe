package com.trio.stride.base

sealed class StrideException(message: String) : Exception(message)

class NetworkException(message: String) : StrideException(message)
class UnauthorizedException(message: String) : StrideException(message)
class NotFoundException(message: String) : StrideException(message)
class IncorrectInfoException(message: String) : StrideException(message)
class UnknownException(message: String) : StrideException(message)