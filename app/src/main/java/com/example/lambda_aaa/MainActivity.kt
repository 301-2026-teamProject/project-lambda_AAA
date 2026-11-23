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
import androidx.compose.material3.Button
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lambda_aaa.controller.ProfileController
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
import androidx.compose.runtime.key
import com.example.lambda_aaa.model.LotteryRepository
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.filled.Email
import com.example.lambda_aaa.controller.AdminNotificationController
import com.example.lambda_aaa.controller.MessagingController
import com.example.lambda_aaa.controller.SearchController
import com.example.lambda_aaa.model.MessagingRepository
import com.example.lambda_aaa.view.AdminNotificationView
import com.example.lambda_aaa.view.MessagingView
import com.example.lambda_aaa.view.NotificationView
import com.example.lambda_aaa.view.Background
import com.example.lambda_aaa.view.HeaderColor
import com.example.lambda_aaa.view.SubText
import com.example.lambda_aaa.view.BorderColor
import com.example.lambda_aaa.model.NotificationRepository
import com.example.lambda_aaa.model.UserRepository
import com.example.lambda_aaa.view.AdminDashboardView
import com.example.lambda_aaa.view.ModerationView
import com.example.lambda_aaa.view.OwnerLotteryView

// ids for testing before identification is fixed
private const val OWNER_ID = "1hQDbywHu0OqUQ0TGWwE"   // owner id
private const val ALEX_ID = "605fNj3nF1gfaxP6cB9Z"    // Alex (entrant) id

private data class ScreenTabs(
    val id: String,
    val label: String,
    val icon: ImageVector
)

private val EntrantTabs = listOf(
    ScreenTabs("EntrantExplore", "Explore", Icons.Filled.Explore),
    ScreenTabs("EntrantLottery", "Lotteries", Icons.Filled.ConfirmationNumber),
    ScreenTabs("EntrantMessage", "Message", Icons.Filled.Email),
    ScreenTabs("EntrantTrips", "Trips", Icons.Filled.EventAvailable),
    ScreenTabs("Profile", "Profile", Icons.Filled.PersonOutline),
)
private val OwnerTabs = listOf(
    ScreenTabs("OwnerHome", "Home", Icons.Outlined.Dashboard),
    ScreenTabs("OwnerCampsites", "Campsites", Icons.Outlined.Forest),
    ScreenTabs("EntrantMessage", "Message", Icons.Filled.Email),    // owner can also message other owners as entrant
    ScreenTabs("OwnerLottery", "Lotteries", Icons.Filled.ConfirmationNumber),
    ScreenTabs("Profile", "Profile", Icons.Filled.PersonOutline),
)

private val AdminTabs = listOf(
    ScreenTabs("AdminDashboard", "Home", Icons.Outlined.Dashboard),
    ScreenTabs("Profile", "Profile", Icons.Filled.PersonOutline),
)

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
    val campsiteRepo = remember { CampsiteRepository() }
    DisposableEffect(campsiteRepo) { onDispose { campsiteRepo.close() } }
    val lotteryRepo = remember { LotteryRepository() }
    DisposableEffect(lotteryRepo) { onDispose { lotteryRepo.close() } }
    val userRepo = remember { UserRepository() }
    DisposableEffect(userRepo) { onDispose { userRepo.close() } }
    val searchRepo = remember { SearchController( campsiteRepo, userRepo ) }
    val messagingRepo = remember { MessagingRepository(campsiteRepo, lotteryRepo) }
    val notificationRepo = remember { NotificationRepository() }

    var installationId by remember {mutableStateOf("")}
    var identifiedUser by remember { mutableStateOf(false) }
    var currentUser: UserProfile = remember {
        UserProfile(
            uid = ALEX_ID,
            name = "Alex Camper",
            email = "alex@example.ca",
            phone = "780-555-0123",
            profilePhotoUrl = "https://picsum.photos/200",
            roles = arrayListOf(Role.ENTRANT, Role.ADMIN),
            isOwner = false,
        )
    } // A default user initialized for first time users

    val profileController = ProfileController()

    LaunchedEffect(Unit) {
        // Adapted from Google for Developers Firebase Documentation, Fundamentals: "Manage Firebase
        // installations and IDs"
        // https://firebase.google.com/docs/projects/manage-installations#kotlin_1
        // Retrieved 2026-10-30
        // Changes: Changed into a try-catch block due to asynchronous issues.
        try {

            installationId = FirebaseInstallations.getInstance().id.await()
            Log.d("Installations", "Installation ID is: $installationId")

        } catch (e: Exception) {
            Log.e("Installations", "Unable to get Installation ID", e)
        }

        try {

            val result =  Firebase.firestore.collection("users")
                .whereEqualTo("deviceId", installationId)
                .get()
                .await()

            // If user does not exist, put placeholder user information into database
            if (result.isEmpty) {
                currentUser.updateDeviceId(installationId)
                Firebase.firestore.collection("users").add(currentUser)
            }

            // Otherwise get user information from database
            else {
                currentUser = result.documents[0].toObject(UserProfile::class.java)!!
            }

            identifiedUser = !identifiedUser

        } catch (e: Exception) {
            Log.e("Identification Error", "Unable to identify user", e)
        }


    }

    if (identifiedUser) {

        val selectedMenu = if (currentUser.roles.contains(Role.ADMIN)) AdminTabs else if (currentUser.roles.contains(Role.OWNER)) OwnerTabs else EntrantTabs
        var selectedTab by remember { mutableStateOf(selectedMenu.first().id) }
        val waitlistRoute = "OwnerLotteryWaitlist/{lotteryId}"
        val lotteryArg = navArgument("lotteryId"){
            type = NavType.StringType
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
                        repo = searchRepo,
                        onCampsiteClick = { navController.navigate("campsite/${Uri.encode(it)}") })
                }
                composable("campsite/{cid}", arguments = listOf(navArgument("cid") { type = NavType.StringType })) { entry ->
                    val cid = entry.arguments?.getString("cid") ?: return@composable
                    val controller = remember(cid, campsiteRepo) { CampsiteDetailController(cid, campsiteRepo) }
                    CampsiteDetailView(controller, onBack = { navController.popBackStack() })
                }
                composable("EntrantLottery") { EntrantProfileView(navController, profileController, currentUser) }
                composable("EntrantTrips") { EntrantProfileView(navController, profileController, currentUser) }
                composable("Profile") { EntrantProfileView(navController, profileController, currentUser) }
                composable("EntrantMessage") {
                    val messagingController = remember { MessagingController(currentUser.uid, Role.ENTRANT, messagingRepo) }
                    MessagingView(messagingController, onBack = {
                        navController.navigate("EntrantExplore")
                        selectedTab = selectedMenu.first().id
                    })
                }

                composable("OwnerHome") {
                    //replaced with currentUser.uid, and added onOpenWaitlist arg to pass
                    com.example.lambda_aaa.view.CampsiteReservationApp(
                        uid = currentUser.uid,
                        onOpenWaitlist = { selectedLotteryId ->
                            navController.navigate("OwnerLotteryWaitlist/$selectedLotteryId")
                        }
                    )
                }
                composable("OwnerCampsites") { EntrantProfileView(navController, profileController, currentUser) }
                composable("OwnerLottery") { OwnerLotteryView(
                    ownerId = currentUser.uid,
                    onOpenWaitlist = { lotteryId ->
                        navController.navigate("OwnerLotteryWaitlist/$lotteryId")
                    }
                ) }
                composable(
                    route = waitlistRoute,
                        arguments = listOf(lotteryArg)) { entry ->
                    val selectedLotteryId = entry.arguments?.getString("lotteryId")

                    if (selectedLotteryId != null) {
                        LotteryWaitlistView(
                            lotteryWaitlistNavController = navController,
                            lotteryId = selectedLotteryId,
                            ownerId = currentUser.uid
                        )
                    }
                }

                composable("AdminDashboard") { AdminDashboardView(navController,
                    repo = searchRepo,
                    onOpenCampsites = { navController.navigate("ModerationView") },
                    onOpenProfiles =  { navController.navigate("ModerationView") },
                    onOpenImages =  { navController.navigate("ModerationView") },
                    )
                }
                composable("ModerationView") { ModerationView(navController,
                    repo = searchRepo,
                    onBack = { navController.navigate("AdminDashboard") },
                    )
                }
                composable("AdminNotificationLogs") { AdminNotificationView(
                    adminNotificationController = AdminNotificationController(notificationRepo)
                ) { navController.popBackStack() }
                }
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
 * Creates specific buttons for each selectable tab at the bottom of the screen.
 *
 * The user can move between the various screens (that are different by role)
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

/** TEMPORARY: test messaging as the owner or as Alex without identification or navigation. */
@Composable
fun DebugApp() {
    var uid by remember { mutableStateOf(OWNER_ID) }
    var screen by remember { mutableStateOf("menu") }

    val campsiteRepo = remember { CampsiteRepository() }
    val lotteryRepo = remember { LotteryRepository() }
    DisposableEffect(Unit) { onDispose { campsiteRepo.close(); lotteryRepo.close() } }
    val messagingRepo = remember { MessagingRepository(campsiteRepo, lotteryRepo) }
    val role = if (uid == OWNER_ID) Role.OWNER else Role.ENTRANT

    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { uid = OWNER_ID; screen = "menu" }) { Text("As owner") }
            Button(onClick = { uid = ALEX_ID; screen = "menu" }) { Text("As entrant") }
        }
        Text("Acting as: $uid ($role)", Modifier.padding(horizontal = 16.dp), fontSize = 12.sp)

        // key() recreates the controllers whenever the identity or screen changes, so lists reload
        key(uid, screen) {
            when (screen) {
                "messages" -> MessagingView(
                    controller = remember { MessagingController(uid, role, messagingRepo) },
                    onBack = { screen = "menu" }
                )
                "notifications" -> NotificationView(userId = uid, onBack = { screen = "menu" })
                else -> Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { screen = "messages" }) {
                        Text(if (role == Role.OWNER) "Message entrants" else "Message owners")
                    }
                    Button(onClick = { screen = "notifications" }) { Text("Notifications") }
                }
            }
        }
    }
}