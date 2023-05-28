package com.example.stepmotor

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.stepmotor.bt.ButtonBluetooth
import com.example.stepmotor.bt.bt
import com.example.stepmotor.screen.home.Home
import com.example.stepmotor.ui.theme.StepMotorwTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import libs.KeepScreenOn


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
                    if (bt.bluetoothAdapter.isEnabled)
                    {

                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            BuildNavGraph()
                        }

                    } else {
                        //Экран включения блютус
                        ButtonBluetooth()
                    }
                }

            }
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
            Home()
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