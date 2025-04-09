package com.trio.stride.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.trio.stride.ui.screens.forgotpassword.ForgotPasswordScreen
import com.trio.stride.ui.screens.home.HomeScreen
import com.trio.stride.ui.screens.login.LoginScreen
import com.trio.stride.ui.screens.signup.SignUpScreen
import com.trio.stride.ui.screens.verifyOtp.VerifyOtpScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Auth.ROUTE
) {
    NavHost(navController, startDestination = startDestination) {
        authGraph(navController)
        composable(route = Screen.Home.route) {
            HomeScreen(
                onLogOutSuccess = {
                    navController.navigate(Screen.Auth.Login.route) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                }
            )
        }
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
                        Screen.Home.route
                    ) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onUnAuthorized = { },
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
