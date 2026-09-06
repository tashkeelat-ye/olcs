package com.olcs.agent.data

import android.content.Context
import java.util.UUID

class DevicePreferences(
    context: Context
) {

    private val prefs =
        context.getSharedPreferences(
            "olcs_config",
            Context.MODE_PRIVATE
        )

    val deviceId: String
        get() {

            var id =
                prefs.getString(
                    "device_id",
                    null
                )

            if (id == null) {

                id =
                    UUID.randomUUID()
                        .toString()

                prefs.edit()
                    .putString(
                        "device_id",
                        id
                    )
                    .apply()
            }

            return id
        }

    var apiUrl: String
        get() =
            prefs.getString(
                "api_url",
                "http://10.0.2.2:3000"
            ) ?: ""

        set(value) {

            prefs.edit()
                .putString(
                    "api_url",
                    value
                )
                .apply()
        }

    var apiKey: String
        get() =
            prefs.getString(
                "api_key",
                ""
            ) ?: ""

        set(value) {

            prefs.edit()
                .putString(
                    "api_key",
                    value
                )
                .apply()
        }

    var smsNumber: String
        get() =
            prefs.getString(
                "sms_number",
                ""
            ) ?: ""

        set(value) {

            prefs.edit()
                .putString(
                    "sms_number",
                    value
                )
                .apply()
        }

    var smsSecret: String
        get() =
            prefs.getString(
                "sms_secret",
                ""
            ) ?: ""

        set(value) {

            prefs.edit()
                .putString(
                    "sms_secret",
                    value
                )
                .apply()
        }
}
