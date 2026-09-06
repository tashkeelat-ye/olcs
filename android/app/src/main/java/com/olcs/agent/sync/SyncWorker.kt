package com.olcs.agent.sync

import android.content.Context

import androidx.room.Room
import androidx.work.*

import com.olcs.agent.data.AppDatabase
import com.olcs.agent.data.DevicePreferences
import com.olcs.agent.network.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import java.time.Instant

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(
    context,
    params
) {

    override suspend fun doWork():
        Result =
        withContext(Dispatchers.IO) {

            val prefs =
                DevicePreferences(
                    applicationContext
                )

            val database =
                Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "olcs.db"
                )
                .build()

            val dao =
                database.locationDao()

            val pending =
                dao.getPending()

            if (
                pending.isEmpty()
            ) {
                return@withContext Result
                    .success()
            }

            if (
                prefs.apiKey.isBlank()
            ) {
                return@withContext Result
                    .failure()
            }

            try {

                val api =
                    NetworkClient.create(
                        prefs.apiUrl
                    )

                val payload =
                    pending.map {

                        LocationPayload(

                            eventId =
                                it.eventId,

                            latitude =
                                it.latitude,

                            longitude =
                                it.longitude,

                            accuracy =
                                it.accuracy,

                            altitude =
                                it.altitude,

                            speed =
                                it.speed,

                            bearing =
                                it.bearing,

                            capturedAt =
                                Instant
                                    .ofEpochMilli(
                                        it.capturedAt
                                    )
                                    .toString()
                        )
                    }

                val response =
                    api.sendLocations(
                        prefs.apiKey,
                        LocationBatchRequest(
                            payload
                        )
                    )

                response.accepted
                    .forEach {

                        dao.updateStatus(
                            it,
                            "SENT",
                            "internet",
                            System
                                .currentTimeMillis()
                        )
                    }

                response.duplicates
                    .forEach {

                        dao.updateStatus(
                            it,
                            "SENT",
                            "internet",
                            System
                                .currentTimeMillis()
                        )
                    }

                Result.success()

            } catch (
                _: Exception
            ) {

                Result.retry()
            }
        }
}
