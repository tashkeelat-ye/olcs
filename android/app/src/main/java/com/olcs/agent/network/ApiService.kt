package com.olcs.agent.network

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST(
        "v1/locations/batch"
    )
    suspend fun sendLocations(

        @Header(
            "X-Device-Api-Key"
        )
        apiKey: String,

        @Body
        body:
            LocationBatchRequest

    ): LocationBatchResponse
}
