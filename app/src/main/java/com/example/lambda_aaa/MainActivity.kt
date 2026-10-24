package com.example.lambda_aaa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.lambda_aaa.ui.theme.CampsiteReservationTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.example.lambda_aaa.view.CampsiteReservationApp

/** Hosts the Compose user interface. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.firestore
        enableEdgeToEdge()
        setContent {
            CampsiteReservationTheme {
                val uid = "123" // get actual uid from auth repository upon login
                CampsiteReservationApp(uid)
            }
        }
    }
}