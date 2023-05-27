package com.example.stepmotor

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.stepmotor.ui.theme.StepMotorwTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import libs.KeepScreenOn
import timber.log.Timber


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        if (!isInitialized)
            Initialization(applicationContext)
        isInitialized = true



        setContent {

            KeepScreenOn()

            val bluetoothPermissions =
                // Checks if the device has Android 12 or above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    rememberMultiplePermissionsState(
                        permissions = listOf(
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN,
                        )
                    )
                } else {
                    rememberMultiplePermissionsState(
                        permissions = listOf(
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                        )
                    )
                }

            StepMotorwTheme {

                if (bluetoothPermissions.allPermissionsGranted) {
                    bt.btIsReady
                    if (bt.bluetoothAdapter.isEnabled) {

                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            //BuildNavGraph(navController)
                        }

                    } else {

                        ButtonBluetooth()

                    }
                }

            }
        }
    }
}

@Composable
private fun ButtonBluetooth() {
    val enableBluetoothContract = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {

        bt.btIsReady = if (it.resultCode == Activity.RESULT_OK) {
            Timber.w("bluetoothLauncher Success")
            true
            //bluetoothPrint.print()
        } else {
            Timber.w("bluetoothLauncher Failed")
            false
        }

    }

    // This intent will open the enable bluetooth dialog
    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)


    Box(modifier = Modifier.fillMaxSize(), Alignment.Center)
    {
        Button(
            onClick = {
                if (!bt.bluetoothAdapter.isEnabled) {
                    // Bluetooth is off, ask user to turn it on
                    enableBluetoothContract.launch(enableBluetoothIntent)
                }
            }) {
            Text(text = "Включить Bluetooth")
        }
    }


}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BuildNavGraph() {

    val navController = rememberAnimatedNavController()

    AnimatedNavHost(
        navController = navController,
        startDestination = "home",
    ) {

        composable(
            "home",
            enterTransition = { fadeIn(animationSpec = tween(0)) },
            exitTransition = { fadeOut(animationSpec = tween(0)) }
        ) {
            //ScreenLazy(navController)
        }

//        composable("info",
//            enterTransition = { fadeIn(animationSpec = tween(0)) },
//            exitTransition = { fadeOut(animationSpec = tween(0)) })
//        {
//            ScreenInfo(navController)
//        }
//
//        composable("web",
//            enterTransition = { fadeIn(animationSpec = tween(0)) },
//            exitTransition = { fadeOut(animationSpec = tween(0)) }
//        ) {
//            Web(navController)
//        }

    }
}