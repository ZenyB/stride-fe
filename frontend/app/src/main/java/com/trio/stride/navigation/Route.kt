package com.trio.stride.navigation

import com.trio.stride.R

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")

    sealed class Auth(route: String) : Screen(route) {
        object Login : Auth("${ROUTE}/login")
        object SignUp : Auth("${ROUTE}/signup")
        object OTP : Auth("${ROUTE}/otp/{userIdentity}") {
            fun createRoute(userIdentity: String): String = "${ROUTE}/otp/$userIdentity"
        }

        object ForgotPassword : Auth("${ROUTE}/forgot_password")

        companion object {
            const val ROUTE = "auth"
        }
    }

    sealed class BottomNavScreen(val route: String, val title: String, val icon: Int?) {
        object Home : BottomNavScreen("home", "Home", R.drawable.home)
        object Maps : BottomNavScreen("maps", "Maps", R.drawable.map)
        object Record : BottomNavScreen("record", "Record", R.drawable.record)
        object Activity : BottomNavScreen("activity", "Activity", R.drawable.activity)
        object Profile : BottomNavScreen("profile", "Profile", R.drawable.user)

        object Search : BottomNavScreen("search", "Search", null)

        companion object {
            val items = listOf(Home, Maps, Record, Activity, Profile)
            const val ROUTE = "main"

        }

    }

    object MainApp : Screen("main")
    object ActivityDetail : Screen("activity/{id}") {
        fun createRoute(id: String): String = "activity/$id"
    }

    object ActivityDetailNoMap : Screen("activity/nomap/{id}") {
        fun createRoute(id: String): String = "activity/nomap/$id"
    }

    object SaveRouteScreen: Screen("user/save-route")

    object EditProfile : Screen("edit_profile")
}