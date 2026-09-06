package com.olcs.agent.location

import android.content.Context

import androidx.room.Room

import com.olcs.agent.data.AppDatabase
import com.olcs.agent.data.LocationEntity

import java.util.UUID

class LocationRepository(
    context: Context
) {

    private val database =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "olcs.db"
        )
        .fallbackToDestructiveMigration()
        .build()

    private val dao =
        database.locationDao()

    suspend fun save(
        latitude: Double,
        longitude: Double,
        accuracy: Float?,
        altitude: Double?,
        speed: Float?,
        bearing: Float?,
        capturedAt: Long,
    ) {

        dao.insert(
            LocationEntity(

                eventId =
                    UUID.randomUUID()
                        .toString(),

                latitude =
                    latitude,

                longitude =
                    longitude,

                accuracy =
                    accuracy,

                altitude =
                    altitude,

                speed =
                    speed,

                bearing =
                    bearing,

                capturedAt =
                    capturedAt,

                createdAt =
                    System.currentTimeMillis()
            )
        )
    }

    suspend fun pending() =
        dao.getPending()

    suspend fun markSent(
        eventId: String,
        transport: String
    ) {

        dao.updateStatus(
            eventId,
            "SENT",
            transport,
            System.currentTimeMillis()
        )
    }

    suspend fun markFailed(
        eventId: String,
        transport: String
    ) {

        dao.updateStatus(
            eventId,
            "FAILED",
            transport,
            System.currentTimeMillis()
        )
    }

    suspend fun pendingCount() =
        dao.pendingCount()
}
