package com.olcs.agent.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient {

    fun create(
        baseUrl: String
    ): ApiService {

        val url =
            if (
                baseUrl.endsWith("/")
            ) {
                baseUrl
            } else {
                "$baseUrl/"
            }

        return Retrofit.Builder()

            .baseUrl(url)

            .addConverterFactory(
                GsonConverterFactory.create()
            )

            .build()

            .create(
                ApiService::class.java
            )
    }
}
