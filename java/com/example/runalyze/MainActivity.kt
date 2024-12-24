package com.example.runalyze

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.runalyze.service.GattClientCallback
import com.example.runalyze.ui.RunalyzeApp
import com.example.runalyze.ui.components.LocationPermissionRequestDialog
import com.example.runalyze.ui.theme.RunalyzeTheme
import com.example.runalyze.utils.RunUtils
import com.example.runalyze.utils.RunUtils.hasAllPermission
import com.example.runalyze.utils.RunUtils.hasLocationPermission
import com.example.runalyze.utils.RunUtils.openAppSetting
import com.example.runalyze.viewmodel.ActivityViewModel
import com.example.runalyze.viewmodel.GoalViewModel
import com.example.runalyze.viewmodel.RunViewModel

class MainActivity : ComponentActivity() {
    private val tag = "Runalyze Debug"

    private val runViewModel: RunViewModel by viewModels { RunViewModel.Factory }
    private val goalViewModel: GoalViewModel by viewModels()
    private val activityViewModel: ActivityViewModel by viewModels()
    private var bluetoothAdapter: BluetoothAdapter? = null

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if there is intent
        val data: Uri? = intent?.data

        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        // check heart rate sensor and connect
        // If version sdk is equal and bigger than 31, check for bluetooth permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // If there is a device connect
                if (bluetoothAdapter?.bondedDevices != null) {
                    for (btDev in bluetoothAdapter?.bondedDevices!!) {
                        Log.d(tag, "bluetooth device bonded is: : ${btDev.name}")
                        if (btDev.name.startsWith("Polar")) {
                            Log.d(tag, "connected to heart rate sensor")
                            val bluetoothGatt =
                                btDev.connectGatt(
                                    this,
                                    false,
                                    GattClientCallback(model = runViewModel)
                                )
                            Log.d(tag, "connect Polar is ${bluetoothGatt.connect()}")
                            break
                        }
                    }
                }
            }

        } else {
            if (bluetoothAdapter?.bondedDevices != null) {
                for (btDev in bluetoothAdapter?.bondedDevices!!) {
                    Log.d(tag, "bluetooth device bonded is: : ${btDev.name}")
                    if (btDev.name.startsWith("Polar")) {
                        Log.d(tag, "connected to heart rate sensor")
                        val bluetoothGatt =
                            btDev.connectGatt(this, false, GattClientCallback(model = runViewModel))
                        Log.d(tag, "connect Polar is ${bluetoothGatt.connect()}")
                        break
                    }
                }
            }
        }

        installSplashScreen()

        setContent {
            RunalyzeTheme {
                // Call the permission request
                PermissionRequester()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RunalyzeApp(goalViewModel, activityViewModel, runViewModel, data)
                }
            }
        }

    }

    // The function checks for granted and declined permissions, displays permission rationale dialogs when necessary,
    // and launches permission requests when permissions are missing
    @RequiresApi(Build.VERSION_CODES.S)
    @Composable
    private fun PermissionRequester() {
        var showPermissionDeclinedRationale by rememberSaveable { mutableStateOf(false) }
        var showRationale by rememberSaveable { mutableStateOf(false) }
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = {
                it.forEach { (permission, isGranted) ->
                    if (!isGranted && RunUtils.locationPermissions.contains(permission)) {
                        showPermissionDeclinedRationale = true
                    }
                }
            }
        )
        if (showPermissionDeclinedRationale)
            LocationPermissionRequestDialog(
                onDismissClick = {
                    if (!hasLocationPermission())
                        finish()
                    else showPermissionDeclinedRationale = false
                },
                onOkClick = { openAppSetting() }
            )
        if (showRationale)
            LocationPermissionRequestDialog(
                onDismissClick = ::finish,
                onOkClick = {
                    showRationale = false
                    permissionLauncher.launch(RunUtils.allPermissions)
                }
            )
        LaunchedEffect(key1 = Unit) {
            when {
                hasAllPermission() -> return@LaunchedEffect
                RunUtils.locationPermissions.any { shouldShowRequestPermissionRationale(it) } -> showRationale =
                    true

                else -> permissionLauncher.launch(RunUtils.allPermissions)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RunalyzeTheme {
        RunalyzeApp()
    }
}