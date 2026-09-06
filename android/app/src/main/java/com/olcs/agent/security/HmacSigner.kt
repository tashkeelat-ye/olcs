package com.olcs.agent.security

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HmacSigner {

    fun sign(
        secret: String,
        payload: String
    ): String {

        val mac =
            Mac.getInstance(
                "HmacSHA256"
            )

        val key =
            SecretKeySpec(
                secret.toByteArray(
                    Charsets.UTF_8
                ),
                "HmacSHA256"
            )

        mac.init(key)

        return mac
            .doFinal(
                payload.toByteArray(
                    Charsets.UTF_8
                )
            )
            .joinToString("") {
                "%02x".format(it)
            }
    }
}
