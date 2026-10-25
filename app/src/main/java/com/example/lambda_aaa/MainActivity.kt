// Defines the Android entry point and the initial Compose application shell.
package com.example.lambda_aaa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable

import androidx.compose.ui.tooling.preview.Preview
import com.example.lambda_aaa.ui.theme.CampsiteReservationTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lambda_aaa.model.CampsiteRepository
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.view.EntrantDashboardView

/** Hosts the Compose user interface. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.firestore
        enableEdgeToEdge()
        val campsiteRepository = CampsiteRepository()
        setContent {
            CampsiteReservationTheme {
                CampsiteReservationApp(
                    campsiteList = campsiteRepository.campsiteList
                )
            }
        }
    }
}

/** Displays the baseline screen until feature navigation is introduced. */
@Composable
fun CampsiteReservationApp(
    campsiteList: List<Campsite>,
) {
    val navController = rememberNavController()

    val userRole = "user" // IMPLEMENT THE DEVICE/PROFILE VERIFICATION SO WE KNOW WHERE TO START A USER

    NavHost(navController = navController, startDestination = userRole) {
        composable("user") { EntrantDashboardView(
            navController,
            campsiteList = campsiteList,
            onNotificationsClick = {
            // Open notifications
            },
            onFiltersClick = {
                // Open campsite filters
            },
            onTabSelected = {
                // Handle bottom navigation
            }
        ) }
        composable("owner") {
            // shortcut: placeholder user ID, replace when profile verification is connected.
            com.example.lambda_aaa.view.CampsiteReservationApp(uid = "123")
        }
        //composable("admin") { AdminDashboardView(navController) }
    }
}

@Preview(showBackground = true)
@Composable
private fun CampsiteReservationAppPreview() {
    CampsiteReservationTheme {
        //CampsiteReservationApp()
    }
}
