package com.olcs.agent.location

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder

import androidx.core.app.NotificationCompat

import com.google.android.gms.location.*

import com.olcs.agent.R

import kotlinx.coroutines.*

class LocationService :
    Service() {

    private lateinit var fused:
        FusedLocationProviderClient

    private lateinit var repository:
        LocationRepository

    private val scope =
        CoroutineScope(
            SupervisorJob() +
                Dispatchers.IO
        )

    private val callback =
        object :
            LocationCallback() {

            override fun onLocationResult(
                result: LocationResult
            ) {

                for (
                    location
                    in result.locations
                ) {

                    scope.launch {

                        repository.save(

                            latitude =
                                location.latitude,

                            longitude =
                                location.longitude,

                            accuracy =
                                location.accuracy,

                            altitude =
                                location.altitude,

                            speed =
                                location.speed,

                            bearing =
                                location.bearing,

                            capturedAt =
                                location.time
                        )
                    }
                }
            }
        }

    override fun onCreate() {

        super.onCreate()

        repository =
            LocationRepository(this)

        fused =
            LocationServices
                .getFusedLocationProviderClient(
                    this
                )

        createChannel()

        startForeground(
            1001,
            notification(),
            ServiceInfo
                .FOREGROUND_SERVICE_TYPE_LOCATION
        )
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        requestUpdates()

        return START_STICKY
    }

    private fun requestUpdates() {

        val request =
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                30_000L
            )
            .setMinUpdateIntervalMillis(
                10_000L
            )
            .setWaitForAccurateLocation(
                false
            )
            .build()

        try {

            fused.requestLocationUpdates(
                request,
                callback,
                mainLooper
            )

        } catch (
            _: SecurityException
        ) {

            stopSelf()
        }
    }

    private fun createChannel() {

        val manager =
            getSystemService(
                NotificationManager::class.java
            )

        manager.createNotificationChannel(
            NotificationChannel(
                "olcs_location",
                "OLCS Location",
                NotificationManager
                    .IMPORTANCE_LOW
            )
        )
    }

    private fun notification():
        Notification {

        return NotificationCompat
            .Builder(
                this,
                "olcs_location"
            )

            .setContentTitle(
                "OLCS"
            )

            .setContentText(
                "تحديد الموقع يعمل"
            )

            .setSmallIcon(
                android.R.drawable
                    .ic_menu_mylocation
            )

            .setOngoing(true)

            .build()
    }

    override fun onDestroy() {

        fused
            .removeLocationUpdates(
                callback
            )

        scope.cancel()

        super.onDestroy()
    }

    override fun onBind(
        intent: Intent?
    ): IBinder? = null
    }
