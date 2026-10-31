// Defines the Android entry point and the initial Compose application shell.
package com.example.lambda_aaa

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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


private enum class ScreenTabs(val label: String) {
    Explore("Explore"),
    Lotteries("Lotteries"),
    Trips("Trips"),
    Profile("Profile"),
} // This is set up in this weird way so that we can dynamically change the number of tabs if we wanted

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

    val testUser = UserProfile(
        name = "Alex Camper",
        email = "alex@example.ca",
        phone = "780-555-0123",
        profilePhotoUrl = "https://picsum.photos/200",
        roles = arrayListOf(Role.ENTRANT),
        creationDate = "2026-10-28")

    val startTab = "owner" // IMPLEMENT THE DEVICE/PROFILE VERIFICATION SO WE KNOW WHERE TO START A USER
    var selectedTab by remember { mutableStateOf(ScreenTabs.Explore) }

    Scaffold(
        containerColor = Background,

        bottomBar = {
            DrawBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    navController.navigate(tab.label)
                },
            )
        }
    ) { innerPadding ->
        NavHost(navController = navController,
            startDestination = startTab,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {

            composable("Explore") { EntrantDashboardView(navController ) }
            composable("Lotteries") { EntrantProfileView(navController, testUser) }
            composable("Trips") { EntrantProfileView(navController, testUser) }
            composable("Profile") { EntrantProfileView(navController, testUser) }

            composable("owner") {
                // shortcut: placeholder user ID, replace when profile verification is connected.
                com.example.lambda_aaa.view.CampsiteReservationApp(uid = "123")
            }
            //composable("admin") { AdminDashboardView(navController) }

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
    selectedTab: ScreenTabs,
    onTabSelected: (ScreenTabs) -> Unit,
) {
    Surface(color = Color.White, shadowElevation = 0.dp) {
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
                ScreenTabs.entries.forEach { tab ->
                    val icon = when (tab) {
                        ScreenTabs.Explore -> Icons.Filled.Explore
                        ScreenTabs.Lotteries -> Icons.Filled.ConfirmationNumber
                        ScreenTabs.Trips -> Icons.Filled.EventAvailable
                        ScreenTabs.Profile -> Icons.Filled.PersonOutline
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clickable {
                                onTabSelected(tab)
                                Log.d("TAG", "$selectedTab")
                            }
                            .padding(top = 7.dp, bottom = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.label,
                            tint = if (tab == selectedTab) HeaderColor else SubText,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = tab.label,
                            color = if (tab == selectedTab) HeaderColor else SubText,
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


@Preview(showBackground = true)
@Composable
private fun CampsiteReservationAppPreview() {
    CampsiteReservationTheme {
        //CampsiteReservationApp()
    }
}
