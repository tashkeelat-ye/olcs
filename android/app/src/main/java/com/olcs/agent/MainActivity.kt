package com.olcs.agent

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.work.*
import com.olcs.agent.location.LocationService
import com.olcs.agent.sync.SyncWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val permissions =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            startTrackingIfAllowed()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                OLCSApp(
                    onStartTracking = {
                        requestPermissions()
                    },
                    onStopTracking = {
                        stopTracking()
                    }
                )
            }
        }

        scheduleSync()
    }

    private fun requestPermissions() {
        permissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.SEND_SMS
            )
        )
    }

    private fun startTrackingIfAllowed() {

        val granted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (!granted) return

        val intent =
            Intent(this, LocationService::class.java)

        ContextCompat.startForegroundService(
            this,
            intent
        )
    }

    private fun stopTracking() {
        stopService(
            Intent(
                this,
                LocationService::class.java
            )
        )
    }

    private fun scheduleSync() {

        val request =
            PeriodicWorkRequestBuilder<SyncWorker>(
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(
                            NetworkType.CONNECTED
                        )
                        .build()
                )
                .build()

        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                "olcs_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }
}

@Composable
private fun OLCSApp(
    onStartTracking: () -> Unit,
    onStopTracking: () -> Unit
) {

    var display by remember {
        mutableStateOf("0")
    }

    var firstNumber by remember {
        mutableStateOf<Double?>(null)
    }

    var operation by remember {
        mutableStateOf<String?>(null)
    }

    var trackingActive by remember {
        mutableStateOf(false)
    }

    var showAdmin by remember {
        mutableStateOf(false)
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "OLCS",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Offline Location Communication System",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor =
                        if (trackingActive)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text = "حالة الموقع",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text =
                                if (trackingActive)
                                    "خدمة تحديد الموقع تعمل"
                                else
                                    "الخدمة متوقفة",
                            fontSize = 13.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                if (trackingActive)
                                    Color(0xFF2E7D32)
                                else
                                    Color.Gray,
                                RoundedCornerShape(50)
                            )
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = display,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    fontSize = 38.sp,
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            val buttons = listOf(
                listOf("7", "8", "9", "÷"),
                listOf("4", "5", "6", "×"),
                listOf("1", "2", "3", "−"),
                listOf("0", ".", "C", "+")
            )

            buttons.forEach { row ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    row.forEach { value ->

                        Button(
                            onClick = {

                                when (value) {

                                    "C" -> {
                                        display = "0"
                                        firstNumber = null
                                        operation = null
                                    }

                                    "+",
                                    "−",
                                    "×",
                                    "÷" -> {
                                        firstNumber =
                                            display.toDoubleOrNull()
                                        operation = value
                                        display = "0"
                                    }

                                    "." -> {
                                        if (!display.contains(".")) {
                                            display += "."
                                        }
                                    }

                                    else -> {
                                        display =
                                            if (display == "0")
                                                value
                                            else
                                                display + value
                                    }
                                }
                            },

                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp),

                            shape =
                                RoundedCornerShape(14.dp)
                        ) {

                            Text(
                                text = value,
                                fontSize = 20.sp
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }

            Button(
                onClick = {

                    val second =
                        display.toDoubleOrNull()

                    val first =
                        firstNumber

                    if (
                        first != null &&
                        second != null &&
                        operation != null
                    ) {

                        val result =
                            when (operation) {

                                "+" -> first + second
                                "−" -> first - second
                                "×" -> first * second
                                "÷" ->
                                    if (second != 0.0)
                                        first / second
                                    else
                                        Double.NaN

                                else -> second
                            }

                        display =
                            if (result.isNaN())
                                "Error"
                            else
                                formatNumber(result)

                        firstNumber = null
                        operation = null
                    }

                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),

                shape = RoundedCornerShape(14.dp)
            ) {

                Text(
                    text = "=",
                    fontSize = 24.sp
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = {
                        trackingActive = true
                        onStartTracking()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            MaterialTheme.colorScheme.primary
                    )
                ) {

                    Text("تشغيل الموقع")
                }

                OutlinedButton(
                    onClick = {
                        trackingActive = false
                        onStopTracking()
                    },
                    modifier = Modifier.weight(1f)
                ) {

                    Text("إيقاف الموقع")
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            TextButton(
                onClick = {
                    showAdmin = true
                }
            ) {

                Text("وضع الإدارة")
            }
        }
    }

    if (showAdmin) {

        AdminPasswordDialog(
            onDismiss = {
                showAdmin = false
            }
        )
    }
}

@Composable
private fun AdminPasswordDialog(
    onDismiss: () -> Unit
) {

    var password by remember {
        mutableStateOf("")
    }

    var error by remember {
        mutableStateOf(false)
    }

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text("وضع الإدارة")
        },

        text = {

            Column {

                Text(
                    "أدخل كلمة مرور الإدارة للوصول إلى إعدادات النظام."
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        error = false
                    },
                    label = {
                        Text("كلمة المرور")
                    },
                    singleLine = true
                )

                if (error) {

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "كلمة المرور غير صحيحة",
                        color =
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        },

        confirmButton = {

            TextButton(
                onClick = {

                    /*
                     * مؤقتاً للمرحلة الأولى.
                     * في المرحلة الأمنية التالية سيتم نقل
                     * كلمة المرور إلى Android Keystore /
                     * secure credential flow.
                     */

                    if (password == "2580") {
                        onDismiss()
                    } else {
                        error = true
                    }
                }
            ) {

                Text("دخول")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("إلغاء")
            }
        }
    )
}

private fun formatNumber(
    value: Double
): String {

    return if (value % 1.0 == 0.0) {
        value.toLong().toString()
    } else {
        value.toString()
    }
}
