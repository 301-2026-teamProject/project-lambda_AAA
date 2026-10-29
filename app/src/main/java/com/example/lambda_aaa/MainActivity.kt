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
import com.example.lambda_aaa.model.Role
import com.example.lambda_aaa.model.UserProfile
import com.example.lambda_aaa.view.EntrantDashboardView
import com.example.lambda_aaa.view.UserProfileView

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

    // To see UserProfileView
    // Uncomment testUser
    // Replace userRole in NavHost with "userProfile"
    // Uncomment the composable in NavHost with route userProfile

    /*
    val testUser = UserProfile(
        name = "Alex Camper",
        email = "alex@example.ca",
        phone = "780-555-0123",
        profilePhotoUrl = "https://picsum.photos/200",
        roles = arrayListOf(Role.ENTRANT),
        creationDate = "2026-10-28")

     */


    val userRole = "user" // IMPLEMENT THE DEVICE/PROFILE VERIFICATION SO WE KNOW WHERE TO START A USER

    NavHost(navController = navController, startDestination = userRole) {

        composable("user") { EntrantDashboardView(
            navController,
            campsiteList = campsiteList,
        ) }
        composable("owner") {
            // shortcut: placeholder user ID, replace when profile verification is connected.
            com.example.lambda_aaa.view.CampsiteReservationApp(uid = "123")
        }
        //composable("admin") { AdminDashboardView(navController) }

        //composable("userProfile") { UserProfileView(navController, testUser) }
    }
}

@Preview(showBackground = true)
@Composable
private fun CampsiteReservationAppPreview() {
    CampsiteReservationTheme {
        //CampsiteReservationApp()
    }
}
