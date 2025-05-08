package com.trio.stride.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.trio.stride.ui.screens.activity.ActivityMainTabScreen
import com.trio.stride.ui.screens.activity.ProfileScreen
import com.trio.stride.ui.screens.activity.view.ActivityDetailNoMapScreen
import com.trio.stride.ui.screens.activity.view.ActivityDetailScreen
import com.trio.stride.ui.screens.forgotpassword.ForgotPasswordScreen
import com.trio.stride.ui.screens.home.HomeScreen
import com.trio.stride.ui.screens.login.LoginScreen
import com.trio.stride.ui.screens.maps.search.SearchMapScreen
import com.trio.stride.ui.screens.maps.view.ViewMapScreen
import com.trio.stride.ui.screens.record.RecordScreen
import com.trio.stride.ui.screens.signup.SignUpScreen
import com.trio.stride.ui.screens.verifyOtp.VerifyOtpScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Auth.ROUTE,
) {
    NavHost(navController, startDestination = startDestination) {
        authGraph(navController)
        mainAppGraph(navController)
    }
}

fun NavGraphBuilder.authGraph(
    navController: NavHostController
) {
    navigation(startDestination = Screen.Auth.Login.route, route = Screen.Auth.ROUTE) {
        composable(Screen.Auth.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(
                        Screen.MainApp.route
                    ) {
                        popUpTo(Screen.MainApp.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onUnAuthorized = { userIdentity ->
                    navController.navigate(Screen.Auth.OTP.createRoute(userIdentity))
                },
                onSignUp = { navController.navigate(Screen.Auth.SignUp.route) },
                onForgotPassword = { navController.navigate(Screen.Auth.ForgotPassword.route) }
            )
        }

        composable(Screen.Auth.SignUp.route) {
            SignUpScreen(navController)
        }

        composable(Screen.Auth.OTP.route) { backStackEntry ->
            val userIdentity = backStackEntry.arguments?.getString("userIdentity") ?: ""
            VerifyOtpScreen(navController, userIdentity)
        }

        composable(Screen.Auth.ForgotPassword.route) {
            ForgotPasswordScreen(
                onChangePasswordSuccess = {
                    navController.navigate(
                        Screen.Auth.Login.route
                    ) {
                        popUpTo(Screen.Auth.ROUTE) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}


fun NavGraphBuilder.mainAppGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.BottomNavScreen.Home.route,
        route = Screen.MainApp.route
    ) {
        composable(Screen.BottomNavScreen.Home.route) {
            HomeScreen(onLogOutSuccess = {
                navController.navigate(Screen.Auth.Login.route) {
                    popUpTo(0)
                    launchSingleTop = true
                }
            })
        }
        composable(Screen.BottomNavScreen.Maps.route) {
            ViewMapScreen(navController)
        }

        composable(Screen.BottomNavScreen.Search.route) {
            SearchMapScreen(navController)
        }

        composable(Screen.BottomNavScreen.Activity.route) {
            ActivityMainTabScreen(navController)
        }

        composable(Screen.BottomNavScreen.Profile.route) {
            ProfileScreen()
        }

        composable(Screen.BottomNavScreen.Record.route) {
            RecordScreen(back = { navController.popBackStack() })
        }

        composable(Screen.ActivityDetail.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            ActivityDetailScreen(id, navController)
        }

        composable(Screen.ActivityDetailNoMap.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            ActivityDetailNoMapScreen(id, navController)
        }
    }

}
