package com.olcs.agent.network

data class LocationPayload(

    val eventId: String,

    val latitude: Double,

    val longitude: Double,

    val accuracy: Float?,

    val altitude: Double?,

    val speed: Float?,

    val bearing: Float?,

    val capturedAt: String
)

data class LocationBatchRequest(
    val locations:
        List<LocationPayload>
)

data class LocationBatchResponse(

    val accepted:
        List<String>,

    val duplicates:
        List<String>,

    val rejected:
        List<String>
)
