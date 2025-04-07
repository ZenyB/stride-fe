package com.trio.stride.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
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
    }
}

fun NavGraphBuilder.authGraph(
    navController: NavHostController
) {
    navigation(startDestination = Screen.Auth.Login.route, route = Screen.Auth.ROUTE) {
        composable(Screen.Auth.Login.route) {
            LoginScreen(
                onLoginSuccess = {},
                onUnAuthorized = {},
                onSignUp = { navController.navigate(Screen.Auth.SignUp.route) }
            )
        }

        composable(Screen.Auth.SignUp.route) {
            SignUpScreen(navController)
        }

        composable(Screen.Auth.OTP.route) { backStackEntry ->
            val userIdentity = backStackEntry.arguments?.getString("userIdentity") ?: ""
            VerifyOtpScreen(navController,  userIdentity)
        }
    }
}
