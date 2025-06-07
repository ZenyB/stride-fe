package com.trio.stride

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.trio.stride.navigation.AppNavHost
import com.trio.stride.navigation.Screen
import com.trio.stride.ui.components.BottomNavBar
import com.trio.stride.ui.theme.StrideTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var navController: NavHostController? = null
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        var route = intent.getStringExtra("navigateTo")
        super.onCreate(savedInstanceState)
        route?.let {
            mainViewModel.sendNavigate(it)
            mainViewModel.clearNavigateTo()
        }
        Log.d("ON_CREATE", "Received: ${mainViewModel.navigateTo.value}")
//        route?.let {
//            if (mainViewModel.navigateTo.value != route)
//                mainViewModel.sendNavigate(it)
//            else route = null
//        }
        enableEdgeToEdge()
        setContent {
            val controller = rememberNavController()
            navController = controller

            var didNavigate by rememberSaveable { mutableStateOf(false) }

            val navigateToRoute by mainViewModel.navigateTo.collectAsState(initial = null)
            val authState by mainViewModel.authState.collectAsStateWithLifecycle()

            val startDestination = remember(authState) {
                when (authState) {
                    MainViewModel.AuthState.UNKNOWN -> null
                    MainViewModel.AuthState.AUTHORIZED -> Screen.MainApp.route
                    MainViewModel.AuthState.UNAUTHORIZED -> Screen.Auth.ROUTE
                    MainViewModel.AuthState.AUTHORIZED_NOT_INITIALIZED -> Screen.Onboarding.route
                }
            }

            LaunchedEffect(route, startDestination) {
                val navRoute = route
                if (!didNavigate && !navRoute.isNullOrBlank() && startDestination != null) {
                    delay(100)
                    Log.d("ON_LEF", "Received: $navRoute")
                    controller.navigate(navRoute) {
                        popUpTo(Screen.BottomNavScreen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                    didNavigate = true
//                    _intent?.removeExtra("navigateTo")
                    mainViewModel.resetNavigateTo()
                }
            }

            LaunchedEffect(navigateToRoute, startDestination) {
                Log.d("ON_NAVIGATE_TO_ROUTE", "Received: $navigateToRoute")
                val navRoute = navigateToRoute
                if (navRoute != null && startDestination != null) {
                    controller.navigate(navRoute) {
                        popUpTo(Screen.BottomNavScreen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
//                    _intent?.removeExtra("navigateTo")
                    mainViewModel.resetNavigateTo()
                }
            }

            val currentBackStack by controller.currentBackStackEntryAsState()
            var showBottomBarState by remember { mutableStateOf(false) }

            LaunchedEffect(currentBackStack) {
                val currentRoute = currentBackStack?.destination?.route
                val bottomRoutes = Screen.BottomNavScreen.items.mapNotNull { it.route }
                val recordRoute = Screen.BottomNavScreen.Record?.route

                showBottomBarState = currentRoute in bottomRoutes && currentRoute != recordRoute
            }

            StrideTheme {
                Scaffold(
                    bottomBar = {
                        Log.i("BOTTOM_BAR", showBottomBarState.toString())
                        if (showBottomBarState) {
                            BottomNavBar(controller)
                        }
                    },
                ) { paddingValues ->
                    startDestination?.let {
                        AppNavHost(
                            navController = controller,
                            startDestination = it,
                            handleBottomBarVisibility = { visible -> showBottomBarState = visible }
                        )
                    }
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()

        val route = intent?.getStringExtra("navigateTo")
        if (route == null) {
            intent?.removeExtra("navigateTo")
            setIntent(null)
        }
//        Log.d("ON_START", "onStart received: $route")
//        route?.let {
//            if (mainViewModel.isNavigated.value == null) {
//                mainViewModel.sendNavigate(it)
////                intent.removeExtra("navigateTo")
//            }
//        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val route = intent.getStringExtra("navigateTo")
        Log.d("ON_NEW_INTENT", "Received: $route")
        route?.let {
            mainViewModel.sendNavigate(it)
            mainViewModel.clearNavigateTo()
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StrideTheme {
        Greeting("Android")
    }
}