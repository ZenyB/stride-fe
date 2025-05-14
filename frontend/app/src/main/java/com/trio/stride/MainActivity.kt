package com.trio.stride

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.trio.stride.navigation.AppNavHost
import com.trio.stride.navigation.Screen
import com.trio.stride.ui.components.BottomNavBar
import com.trio.stride.ui.components.Loading
import com.trio.stride.ui.theme.StrideTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val mainViewModel: MainViewModel by viewModels()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val authState by mainViewModel.authState.collectAsState()
            val currentBackStack by navController.currentBackStackEntryAsState()
            var startDestination: String? = null

            var showBottomBarState by remember { mutableStateOf(false) }

            LaunchedEffect(currentBackStack) {
                showBottomBarState =
                    (currentBackStack?.destination?.route in Screen.BottomNavScreen.items.map { it.route }) &&
                            currentBackStack?.destination?.route != Screen.BottomNavScreen.Record.route
            }

            StrideTheme {
                when (authState) {
                    MainViewModel.AuthState.UNKNOWN -> {
                        Loading()
                    }

                    MainViewModel.AuthState.AUTHORIZED -> {
                        startDestination = Screen.MainApp.route
                    }

                    MainViewModel.AuthState.UNAUTHORIZED -> {
                        startDestination = Screen.Auth.ROUTE
                    }

                    MainViewModel.AuthState.AUTHORIZED_NOT_INITIALIZED -> {
                        startDestination = Screen.Onboarding.route
                    }
                }
                Scaffold(
                    bottomBar = {
                        Log.i("BOTTOM_BAR", showBottomBarState.toString())
                        if (showBottomBarState) {
                            BottomNavBar(navController)
                        }
                    },
                ) { paddingValues ->
                    startDestination?.let {
                        AppNavHost(
                            navController = navController,
                            startDestination = it,
                            handleBottomBarVisibility = { visible -> showBottomBarState = visible }
                        )
                    }
                }
            }
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