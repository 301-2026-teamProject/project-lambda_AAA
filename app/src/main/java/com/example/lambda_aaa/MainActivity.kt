// Defines the Android entry point and the initial Compose application shell.
package com.example.lambda_aaa

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.lambda_aaa.view.CampsiteReservationApp
import com.example.lambda_aaa.view.EntrantDashboardView
import com.example.lambda_aaa.view.EntrantProfileView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.installations.FirebaseInstallations

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
                    campsiteList = campsiteRepository.campsiteList,
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

    var installationID by remember {mutableStateOf("")}
    var identifiedUser by remember { mutableStateOf(false) }
    var currentUser = remember {
        UserProfile(
            deviceID = installationID,
            name = "Alex Camper",
            email = "alex@example.ca",
            phone = "780-555-0123",
            profilePhotoUrl = "https://picsum.photos/200",
            roles = arrayListOf(Role.ENTRANT),
            creationDate = "2026-10-28"
        )
    }

    LaunchedEffect(Unit) {
        // Adapted from Google for Developers Firebase Documentation, Fundamentals: "Manage Firebase
        // installations and IDs"
        // https://firebase.google.com/docs/projects/manage-installations#kotlin_1
        // Retrieved 2026-10-30
        // Changes: Changed one line and added two to do device based identification for users
        FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                installationID = task.result
                Log.d("Installations", "Installation ID is: $installationID")
            } else {
                Log.e("Installations", "Unable to get Installation ID")
            }
        }

        Firebase.firestore.collection("Users")
            .whereEqualTo("deviceID", installationID)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {

                    Firebase.firestore.collection("Users")
                        .document(currentUser.userName)
                        .set(currentUser)
                }

                currentUser = result.toObject(UserProfile::class.java)
                identifiedUser = !identifiedUser
            }
    }

    if (identifiedUser) {


        val userRole = "user" // IMPLEMENT THE DEVICE/PROFILE VERIFICATION SO WE KNOW WHERE TO START A USER

        NavHost(navController = navController, startDestination = userRole) {

            composable("user") {
                EntrantDashboardView(
                    navController,
                    campsiteList = campsiteList,
                )
            }
            composable("Explore") {
                EntrantDashboardView(
                    navController,
                    campsiteList = campsiteList,
                )
            }
            composable("Lotteries") { EntrantProfileView(navController, currentUser) }
            composable("Trips") { EntrantProfileView(navController, currentUser) }
            composable("Profile") { EntrantProfileView(navController, currentUser) }

            composable("owner") {
                // shortcut: placeholder user ID, replace when profile verification is connected.
                CampsiteReservationApp(uid = "123")
            }
            //composable("admin") { AdminDashboardView(navController) }

        }

    }

    else {

        Column (
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Identifying user...", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            CircularProgressIndicator()
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun CampsiteReservationAppPreview() {
    CampsiteReservationTheme {
        //CampsiteReservationApp()
    }
}
