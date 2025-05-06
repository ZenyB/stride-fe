package com.trio.stride.data

object ApiConstants {
    const val BASE_URL = "http://103.211.201.40:8080/api/v1/"
    const val IDENTITY_URL = "${BASE_URL}identity/"
    const val PROFILE_URL = "${BASE_URL}profile/"
    const val ROUTE_URL = "${BASE_URL}route/"
    const val CORE_URL = "${BASE_URL}core/"
    const val BRIDGE_URL = "${BASE_URL}bridge/"
    const val LOGIN_END_POINTS = "auth/login"
    const val LOGIN_GOOGLE = "auth/login/google"
    const val LOGOUT = "auth/logout"
    const val PROFILE = "users/profile"
    const val SIGNUP = "users/register"
    const val RESET_PASSWORD = "users/reset-password"
    const val USERS = "users"
    const val RECOMMEND = "stride-routes/recommend"
    const val SPORT = "sports"
    const val CATEGORY = "categories"
    const val ACTIVITY = "activities"
    const val FILE = "files"
}