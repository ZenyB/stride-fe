package com.trio.stride.navigation

sealed class Screen(val route: String) {
    sealed class Auth(route: String) : Screen(route) {
        object Login : Auth("${ROUTE}/login")
        object SignUp : Auth("${ROUTE}/signup")
        object OTP : Auth("${ROUTE}/otp/{userIdentity}") {
            fun createRoute(userIdentity: String): String = "${ROUTE}/otp/$userIdentity"
        }

        companion object {
            const val ROUTE = "auth"
        }
    }

    object Home : Screen("home")
}