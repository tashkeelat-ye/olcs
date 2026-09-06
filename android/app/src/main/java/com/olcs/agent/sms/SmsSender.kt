package com.olcs.agent.sms

import android.telephony.SmsManager

import com.olcs.agent.data.LocationEntity
import com.olcs.agent.security.HmacSigner

class SmsSender {

    fun send(
        phoneNumber: String,
        location: LocationEntity,
        deviceId: String,
        secret: String,
    ) {

        val payload =
            "OL1" +
            "|D=$deviceId" +
            "|E=${location.eventId}" +
            "|L=${location.latitude},${location.longitude}" +
            "|A=${location.accuracy ?: 0}" +
            "|T=${location.capturedAt}"

        val signature =
            HmacSigner.sign(
                secret,
                payload
            )

        val message =
            "$payload|S=$signature"

        SmsManager
            .getDefault()
            .sendTextMessage(
                phoneNumber,
                null,
                message,
                null,
                null
            )
    }
}
