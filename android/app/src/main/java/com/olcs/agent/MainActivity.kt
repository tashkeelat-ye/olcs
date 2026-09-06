package com.olcs.agent

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.core.content.ContextCompat

import androidx.work.*

import com.olcs.agent.location.LocationService
import com.olcs.agent.sync.SyncWorker

import java.util.concurrent.TimeUnit

class MainActivity :
    ComponentActivity() {

    private val permissions =
        registerForActivityResult(
            ActivityResultContracts
                .RequestMultiplePermissions()
        ) {
            startTrackingIfAllowed()
        }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        setContent {

            MaterialTheme {

                Column(

                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(24.dp)

                ) {

                    Text(
                        text = "OLCS",
                        style =
                            MaterialTheme
                                .typography
                                .headlineLarge
                    )

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Text(
                        "Offline Location Communication System"
                    )

                    Spacer(
                        Modifier.height(30.dp)
                    )

                    Button(
                        onClick = {
                            requestPermissions()
                        },

                        modifier =
                            Modifier
                                .fillMaxWidth()
                    ) {

                        Text(
                            "تشغيل التتبع"
                        )
                    }

                    Spacer(
                        Modifier.height(12.dp)
                    )

                    OutlinedButton(

                        onClick = {
                            stopTracking()
                        },

                        modifier =
                            Modifier
                                .fillMaxWidth()
                    ) {

                        Text(
                            "إيقاف التتبع"
                        )
                    }
                }
            }
        }

        scheduleSync()
    }

    private fun requestPermissions() {

        permissions.launch(

            arrayOf(

                Manifest.permission
                    .ACCESS_FINE_LOCATION,

                Manifest.permission
                    .ACCESS_COARSE_LOCATION,

                Manifest.permission
                    .SEND_SMS
            )
        )
    }

    private fun startTrackingIfAllowed() {

        val granted =
            ContextCompat
                .checkSelfPermission(
                    this,
                    Manifest.permission
                        .ACCESS_FINE_LOCATION
                ) ==
                PackageManager
                    .PERMISSION_GRANTED

        if (!granted) {
            return
        }

        val intent =
            Intent(
                this,
                LocationService::class.java
            )

        ContextCompat
            .startForegroundService(
                this,
                intent
            )
    }

    private fun stopTracking() {

        stopService(
            Intent(
                this,
                LocationService::class.java
            )
        )
    }

    private fun scheduleSync() {

        val request =
            PeriodicWorkRequestBuilder<
                SyncWorker
            >(
                15,
                TimeUnit.MINUTES
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    )
                    .build()
            )
            .build()

        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(

                "olcs_sync",

                ExistingPeriodicWorkPolicy
                    .KEEP,

                request
            )
    }
    }
