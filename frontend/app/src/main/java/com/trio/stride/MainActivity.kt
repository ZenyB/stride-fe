package com.trio.stride

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.trio.stride.navigation.AppNavHost
import com.trio.stride.navigation.Screen
import com.trio.stride.ui.components.BottomNavBar
import com.trio.stride.ui.components.Loading
import com.trio.stride.ui.theme.StrideTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var bluetoothAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        val mainViewModel: MainViewModel by viewModels()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val authState by mainViewModel.authState.collectAsState()
            val currentBackStack by navController.currentBackStackEntryAsState()
            val showBottomBar =
                currentBackStack?.destination?.route in Screen.BottomNavScreen.items.map { it.route }
            var startDestination: String? = null

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
                }
                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(navController)
                        }
                    },
                ) { paddingValues ->
                    startDestination?.let {
                        AppNavHost(
                            navController = navController,
                            startDestination = Screen.BottomNavScreen.ROUTE,
                            onBluetoothStateChanged = {
                                checkAndRequestPermissions()
                            }
                        )

                    },
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(navController)
                        }
                    }
                )

            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkAndRequestPermissions()
    }

    private var isBluetoothDialogAlreadyShown = false
    private fun showBluetoothDialog() {
        if (!bluetoothAdapter.isEnabled) {
            if (!isBluetoothDialogAlreadyShown) {
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startBluetoothIntentForResult.launch(enableBluetoothIntent)
                isBluetoothDialogAlreadyShown = true
            }

        }
    }

    private val startBluetoothIntentForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            isBluetoothDialogAlreadyShown = false
            if (result.resultCode != Activity.RESULT_OK) {
                showBluetoothDialog()
            }
        }

    private val requestBluetoothPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[android.Manifest.permission.BLUETOOTH_CONNECT] == true &&
                permissions[android.Manifest.permission.BLUETOOTH_SCAN] == true
            ) {
                // Permissions granted, now check Bluetooth status
                showBluetoothDialog()
            } else {
                // Permissions denied
                Toast.makeText(this, "Bluetooth permissions denied.", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkAndRequestPermissions() {
        val bluetoothConnectPermission = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.BLUETOOTH_CONNECT
        )
        val bluetoothScanPermission = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.BLUETOOTH_SCAN
        )

        if (bluetoothConnectPermission == PackageManager.PERMISSION_GRANTED &&
            bluetoothScanPermission == PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions already granted, proceed to enable Bluetooth
            showBluetoothDialog()
        } else {
            // Request permissions
            requestBluetoothPermissionsLauncher.launch(
                arrayOf(
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.BLUETOOTH_SCAN
                )
            )
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