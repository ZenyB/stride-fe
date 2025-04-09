package com.trio.stride

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.trio.stride.navigation.AppNavHost
import com.trio.stride.navigation.Screen
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
            val isLoggedIn by mainViewModel.isLoggedIn.collectAsState()

            StrideTheme {

                Scaffold(
                    content = { paddingValues ->
                        // NavHost for handling navigation
                        val startDestination =
                            if (isLoggedIn) Screen.Home.route else Screen.Auth.ROUTE
                        AppNavHost(
                            navController = navController,
                            startDestination = startDestination
                        )
                    }
                )

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