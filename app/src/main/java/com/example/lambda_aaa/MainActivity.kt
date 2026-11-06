// Defines the Android entry point and the initial Compose application shell.
package com.example.lambda_aaa

import android.os.Bundle
import android.util.Log
import android.net.Uri
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.lambda_aaa.model.CampsiteRepository
import com.example.lambda_aaa.controller.CampsiteDetailController
import com.example.lambda_aaa.view.CampsiteDetailView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.example.lambda_aaa.model.Role
import com.example.lambda_aaa.model.UserProfile
import com.example.lambda_aaa.ui.theme.CampsiteReservationTheme
import com.example.lambda_aaa.view.EntrantDashboardView
import com.example.lambda_aaa.view.EntrantProfileView
import com.example.lambda_aaa.view.LotteryWaitlistView
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.coroutines.tasks.await



private data class ScreenTabs(
    val id: String,
    val label: String,
    val icon: ImageVector
)

private val EntrantTabs = listOf(
    ScreenTabs("EntrantExplore", "Explore", Icons.Filled.Explore),
    ScreenTabs("EntrantLottery", "Lotteries", Icons.Filled.ConfirmationNumber),
    ScreenTabs("EntrantTrips", "Trips", Icons.Filled.EventAvailable),
    ScreenTabs("Profile", "Profile", Icons.Filled.PersonOutline),
)

private val OwnerTabs = listOf(
    ScreenTabs("OwnerHome", "Home", Icons.Outlined.Dashboard),
    ScreenTabs("OwnerCampsites", "Campsites", Icons.Outlined.Forest),
    ScreenTabs("OwnerLottery", "Lotteries", Icons.Filled.ConfirmationNumber),
    ScreenTabs("Profile", "Profile", Icons.Filled.PersonOutline),
)

private val Background = Color(0xFFF3F0E9) // Used for the background and Campsite icon. Bone-White color.
private val HeaderColor = Color(0xFF242823) // Used for Headers. Muted Black.
private val SubText = Color(0xFF6E716C) // Used for smaller text and Icons. Dark Gray.
private val BorderColor = Color(0xFFDEDCD4) // Used for borders. A muted equivalent of the Background color.

/** Hosts the Compose user interface. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.firestore
        enableEdgeToEdge()
        setContent {
            CampsiteReservationTheme {
                CampsiteReservationApp()
            }
        }
    }
}

/** Displays the baseline screen until feature navigation is introduced. */
@Composable
fun CampsiteReservationApp(
) {
    val navController = rememberNavController()
    val repository = remember { CampsiteRepository() }
    DisposableEffect(repository) { onDispose { repository.close() } }

    var installationID by remember {mutableStateOf("")}
    var identifiedUser by remember { mutableStateOf(false) }
    var currentUser: UserProfile = remember {
        UserProfile(
            name = "Alex Camper",
            email = "alex@example.ca",
            phone = "780-555-0123",
            profilePhotoUrl = "https://picsum.photos/200",
            roles = arrayListOf(Role.ENTRANT),
            creationDate = "2026-10-28"
        )
    } // A default user initialized for first time users

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
        }.await()

        // Search for user in database
        Firebase.firestore.collection("users")
            .whereEqualTo("userDeviceID", installationID)
            .get()
            .addOnSuccessListener { result ->

                // If user does not exist, put placeholder user information into database
                if (result.isEmpty) {
                    currentUser.updateDeviceID(installationID)
                    Firebase.firestore.collection("users")
                        .add(currentUser)
                }

                // Otherwise get user information from database
                else {
                    currentUser = result.documents[0].toObject(UserProfile::class.java)!!
                }

                identifiedUser = !identifiedUser
            }
            .addOnFailureListener { error ->
                Log.w("Listener", "Error is", error)
            }
    }

    if (identifiedUser) {

        var selectedMenu = EntrantTabs
        var selectedTab by remember { mutableStateOf(selectedMenu.first().id) }
        if (currentUser.userRole == Role.OWNER) {
            selectedMenu = OwnerTabs
            selectedTab = "OwnerHome"
        }

        Scaffold(
            containerColor = Background,

            bottomBar = {
                DrawBottomBar(
                    tabs = selectedMenu,
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        selectedTab = tab.id
                        navController.navigate(tab.id)
                    },
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = selectedMenu.first().id,
                //startDestination = "OwnerLotteryWaitlist",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {

                composable("EntrantExplore") {
                    EntrantDashboardView(navController,
                        onCampsiteClick = { navController.navigate("campsite/${Uri.encode(it)}") })
                }
                composable("campsite/{cid}", arguments = listOf(navArgument("cid") { type = NavType.StringType })) { entry ->
                    val cid = entry.arguments?.getString("cid") ?: return@composable
                    val controller = remember(cid, repository) { CampsiteDetailController(cid, repository) }
                    CampsiteDetailView(controller, onBack = { navController.popBackStack() })
                }
                composable("EntrantLottery") { EntrantProfileView(navController, currentUser) }
                composable("EntrantTrips") { EntrantProfileView(navController, currentUser) }
                composable("Profile") { EntrantProfileView(navController, currentUser) }

                composable("OwnerHome") {
                    // shortcut: placeholder user ID, replace when profile verification is connected.
                    com.example.lambda_aaa.view.CampsiteReservationApp(uid = "123")
                }
                composable("OwnerCampsites") { EntrantProfileView(navController, currentUser) }
                composable("OwnerLottery") { EntrantProfileView(navController, currentUser) }
                composable("OwnerLotteryWaitlist") { LotteryWaitlistView(navController) }
                //composable("admin") { AdminDashboardView(navController) }

            }
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

/**
 * Creates specific buttons for each selectable tab in EntrantView.
 *
 * The user can move between the Explore, Lotteries, Trips, and Profiles screens
 * by pressing their respective buttons.
 *
 * @return nothing
 */
@Composable
private fun DrawBottomBar(
    tabs: List<ScreenTabs>,
    selectedTab: String,
    onTabSelected: (ScreenTabs) -> Unit
) {
    if (tabs.isNotEmpty()) {
        // If the previously selected tab isn't available then we highlight the first available tab instead
        val effectiveSelectedTab = tabs.firstOrNull { it.id == selectedTab }?.id

        Surface(
            color = Color.White,
            shadowElevation = 0.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(color = BorderColor, thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .height(62.dp)
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    tabs.forEach { tab ->
                        val selected = tab.id == effectiveSelectedTab
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clickable {
                                    onTabSelected(tab)
                                }
                                .padding(top = 7.dp, bottom = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                tint = if (selected) HeaderColor else SubText,
                                modifier = Modifier.size(18.dp),
                            )
                            Text(
                                text = tab.label,
                                color = if (selected) HeaderColor else SubText,
                                fontSize = 10.sp,
                                lineHeight = 12.sp,
                                fontWeight = FontWeight.Normal,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
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
