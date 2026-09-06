package com.olcs.agent.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "location_events"
)
data class LocationEntity(

    @PrimaryKey
    val eventId: String,

    val latitude: Double,

    val longitude: Double,

    val accuracy: Float?,

    val altitude: Double?,

    val speed: Float?,

    val bearing: Float?,

    val capturedAt: Long,

    val createdAt: Long,

    val status: String = "PENDING",

    val attempts: Int = 0,

    val lastAttemptAt: Long? = null,

    val transport: String? = null
)
