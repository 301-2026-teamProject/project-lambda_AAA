package com.example.lambda_aaa.view

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@Composable
fun QrScreenView(
    qrScreenNavController : NavController,
) {

    var startScanning by remember { mutableStateOf(false) }
    var successfulScan by remember  { mutableStateOf(false) }
    var unsuccessfulScan by remember  { mutableStateOf(false) }
    var codeData by remember { mutableStateOf("")}

    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(innerPadding),
        ) {

            // This Column encapsulates the rest of the Screen
            // Also the ENTIRE THING is scrollable so it can be used horizontally as well :)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Text(
                        text = "ENTRANT · DISCOVER",
                        color = SubText,
                        fontSize = 10.sp,
                        letterSpacing = 0.75.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 20.dp),
                    )
                    Text(
                        text = "Find your next campsite",
                        color = HeaderColor,
                        fontSize = 16.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 3.dp),
                    )
                }

                // Code for QR Code Scanner starts here
                Column (modifier = Modifier.fillMaxSize().padding(8.dp)) {

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "QR Code Scan",
                        color = Color(0xFF06402B),
                        fontSize = 24.sp
                    )

                    HorizontalDivider(
                        thickness = 4.dp,
                        color = Color(0xFF06402B)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Start scan using the button below and align the campsite QR code inside the frame.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            startScanning = !startScanning
                            successfulScan = true
                            unsuccessfulScan = false

                            // Adapted from Google for Developers ML Kit, Google code scanner (Android only)
                            // https://developers.google.com/ml-kit/vision/barcode-scanning/code-scanner#kotlin
                            // Retrieved 2026-11-26
                            // Changes: No changes, just added functionality relevant to the workings of the app
                            val scannerOptions = GmsBarcodeScannerOptions.Builder()
                                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                                .enableAutoZoom()
                                .build()

                            val qrCodeScanner = GmsBarcodeScanning.getClient(context, scannerOptions)

                            qrCodeScanner.startScan()
                                .addOnSuccessListener { qrCode ->
                                    codeData = qrCode.rawValue.toString()

                                }
                                .addOnCanceledListener {

                                }
                                .addOnFailureListener { e ->
                                    Log.e("QR Code Scan Failed", "Failed to scan QR code", e)
                                }

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White, contentColor = Color.Black),
                        shape = RectangleShape,
                        border = BorderStroke(width = 1.5.dp, color = Color(0xFF9BD3AE))
                    ) {Text("SCAN QR CODE")}

                    Spacer(modifier = Modifier.height(16.dp))

                    if (successfulScan) {
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Success:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            TextButton(onClick = {}) {
                                Text(
                                    "open matching campsite details and active lottery",
                                    fontSize = 16.sp
                                )
                            }
                        }

                        Text(
                            "Code: $codeData",
                            fontSize = 16.sp
                        )

                    } else if (unsuccessfulScan) {
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Invalid code.",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.Red
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Text(
                                "Scan a campsite promotional code",
                                fontSize = 16.sp,
                                color = Color.Red
                            )

                        }
                    }

                }


            }
        }

    }
}
