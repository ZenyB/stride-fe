package com.trio.stride.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.trio.stride.ui.screens.activity.ActivityMainTabScreen
import com.trio.stride.ui.screens.activity.view.ActivityDetailNoMapScreen
import com.trio.stride.ui.screens.activity.view.ActivityDetailScreen
import com.trio.stride.ui.screens.forgotpassword.ForgotPasswordScreen
import com.trio.stride.ui.screens.goal.create.CreateGoalScreen
import com.trio.stride.ui.screens.goal.edit.EditGoalScreen
import com.trio.stride.ui.screens.goal.view.GoalListScreen
import com.trio.stride.ui.screens.home.HomeScreen
import com.trio.stride.ui.screens.login.LoginScreen
import com.trio.stride.ui.screens.maps.saveroute.SaveRouteScreen
import com.trio.stride.ui.screens.maps.search.SearchMapScreen
import com.trio.stride.ui.screens.maps.view.ViewMapScreen
import com.trio.stride.ui.screens.onboarding.OnboardingScreen
import com.trio.stride.ui.screens.profile.ProfileScreen
import com.trio.stride.ui.screens.record.RecordScreen
import com.trio.stride.ui.screens.signup.SignUpScreen
import com.trio.stride.ui.screens.verifyOtp.VerifyOtpScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Auth.ROUTE,
    handleBottomBarVisibility: (Boolean) -> Unit,
) {
    NavHost(navController, startDestination = startDestination) {
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(navigateToHome = {
                navController.navigate(
                    Screen.MainApp.route
                ) {
                    popUpTo(Screen.MainApp.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            })
        }
        authGraph(navController)
        mainAppGraph(navController, handleBottomBarVisibility = { handleBottomBarVisibility(it) })
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
    navController: NavHostController,
    handleBottomBarVisibility: (Boolean) -> Unit
) {
    navigation(
        startDestination = Screen.BottomNavScreen.Home.route,
        route = Screen.MainApp.route
    ) {
        composable(Screen.BottomNavScreen.Home.route) {
            HomeScreen(navController, onLogOutSuccess = {
                navController.navigate(Screen.Auth.Login.route) {
                    popUpTo(0)
                    launchSingleTop = true
                }
            })
        }
        composable(Screen.BottomNavScreen.Maps.route) {
            ViewMapScreen(navController, startRecord = { geometry ->
                navController.navigate("${Screen.BottomNavScreen.Record.route}?geometry=$geometry") {
                    popUpTo(Screen.BottomNavScreen.Home.route) { inclusive = false }
                    launchSingleTop = true
                }
            })
        }

        composable(Screen.BottomNavScreen.Search.route) {
            SearchMapScreen(navController)
        }

        composable(Screen.BottomNavScreen.Activity.route) {
            ActivityMainTabScreen(navController)
        }

        composable(Screen.BottomNavScreen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                handleBottomBarVisibility = { handleBottomBarVisibility(it) })
        }

        composable(
            route = Screen.BottomNavScreen.Record.route + "?geometry={geometry}",
            arguments = listOf(navArgument("geometry") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
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

        composable(Screen.SaveRouteScreen.route) {
            SaveRouteScreen(navController)
        }
        composable(Screen.CreateGoalScreen.route) {
            CreateGoalScreen(navController)
        }
        composable(Screen.GoalListScreen.route) {
            GoalListScreen(navController)
        }
        composable(
            Screen.EditGoalScreen.route,
            arguments = listOf(navArgument("data") { type = NavType.StringType })
        ) { backStackEntry ->
            val data = backStackEntry.arguments?.getString("data") ?: ""
            EditGoalScreen(navController, data)
        }
    }

}
