package com.example.lambda_aaa.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.controller.CampsiteOwnerController
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Background = Color(0xFFF3F0E9) // Used for the background and Campsite icon. Bone-White color.
private val HeaderColor = Color(0xFF242823) // Used for Headers. Muted Black.
private val SubText = Color(0xFF6E716C) // Used for smaller text and Icons. Dark Gray.
private val BorderColor = Color(0xFFDEDCD4) // Used for borders. A muted equivalent of the Background color.
private val FilterSelectColor = Color(0xFFE1EADF) // Color chosen for filter boxes. A darker version of the Background color.
private val SelectColor = Color(0xFF526B5A) // Color chosen for other selected elements. A nice Green.
private val AddGreen = Color(0xFF315E43) // The big green add button

@Composable
fun CampsiteOwnerView(
    uid: String,
    controller: CampsiteOwnerController,
    onClickRegister: () -> Unit,  // clicking register lead user to register page
    onClickCampsite: (String) -> Unit,  // clicking an owned campsite lead user to edit page
    editMessage: Pair<Boolean, String>?,
    onEditMessageShown: () -> Unit
) {
    val filteredCampsites = controller.campsites.filter { campsite ->
        val matchID = (campsite.ownerId == uid)
        matchID
    }
    LaunchedEffect(Unit) {
        controller.refresh()
    }
    CampsiteOwnerContent(
        campsiteEditMessage = editMessage,
        onMessageShown = onEditMessageShown,
        isLoading = controller.isLoading,
        campsites = filteredCampsites,
        errorMessage = controller.errorMessage,
        onClickRegister = onClickRegister,
        onClickCampsite = onClickCampsite
    )
}

//@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampsiteOwnerContent(
    campsiteEditMessage: Pair<Boolean, String>?,
    onMessageShown: () -> Unit,
    isLoading: Boolean,
    campsites: List<Campsite>,
    errorMessage: String?,
    onClickRegister: () -> Unit,
    onClickCampsite: (String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(campsiteEditMessage) {
        campsiteEditMessage?.let {
            snackbarHostState.showSnackbar(it.second)
            onMessageShown()
        }
    }

    Scaffold(
        containerColor = Background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding  ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Background)
        ) {

            // TOP HEADER
            TopBar()
            HorizontalDivider(color = BorderColor, thickness = 1.dp)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp, bottom = 20.dp),

                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // DASHBOARD GREETING
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    Text(
                        text = "OWNER DASHBOARD",
                        fontSize = 10.sp,
                        color = SubText,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.4.sp
                    )

                    Text(
                        text = "Good afternoon, (user)",
                        fontSize = 16.sp,
                        color = HeaderColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                RegisterCampsiteButton(
                    onClick = onClickRegister
                )

                when {
                    isLoading -> Box(Modifier.fillMaxSize().padding(innerPadding), Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    errorMessage != null -> Box(Modifier.fillMaxSize().padding(innerPadding), Alignment.Center) {
                        Text(errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                    else -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (campsites.isEmpty()) {
                            Text("You haven't registered any campsites yet.")
                        }
                        campsites.forEach { campsite ->
                            CampsiteCard(
                                campsite = campsite, // just sends the entire object which might not be efficient, but it works
                                onClick = { onClickCampsite(campsite.cid)}, // PUT CLICK ACTION HERE
                            )
                            HorizontalDivider(color = BorderColor, thickness = 1.dp)
                        }
                    }
                }

                // Attention notifications ig
                Text(
                    text = "Needs attention",
                    fontSize = 15.sp,
                    color = HeaderColor,
                    fontWeight = FontWeight.SemiBold
                )

                AttentionCard(
                    title = "Notification",
                    description = "description",
                    onClick = {} // onLotteryClick
                )

                AttentionCard(
                    title = "New reservation",
                    description = "May 14–16 · Jeremy Clarkson????",
                    onClick = {} // onReservationClick
                )
            }
        }
    }
}

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // This is just the thing that states your role
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Terrain,
                contentDescription = null,
                tint = HeaderColor,
                modifier = Modifier.size(19.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Campsite Owner",
                color = HeaderColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(modifier = Modifier.weight(1f))

        // The notification button
        Surface(
            //onClick = onNotificationsClick, // IMPLEMENT THIS NOTIFICATION BUTTON LATER
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.size(40.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.NotificationsNone,
                    contentDescription = "Notifications",
                    tint = HeaderColor,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun RegisterCampsiteButton(onClick: () -> Unit) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(57.dp)
            .clickable {
                onClick() // You can click it yaaay
            },
        color = AddGreen,
        shape = RoundedCornerShape(13.dp)
    ) {
        // Used for the text boxes
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // The goofy plus Icon on the left
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Add campsite",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 15.dp)
                    .size(19.dp)
            )

            // button text
            Text(
                text = "Register campsite",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun CampsiteCard(campsite: Campsite, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick() // You can click it to open the menu
            },
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, BorderColor)
    ) {

        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // campsite name and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = campsite.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HeaderColor
                )

                // ACTIVE STATUS LABEL
                Surface(
                    color = FilterSelectColor,
                    shape = RoundedCornerShape(50.dp)
                ) {

                    Text(
                        text = "Active",
                        fontSize = 11.sp,
                        color = HeaderColor,
                        modifier = Modifier.padding(
                            horizontal = 11.dp,
                            vertical = 5.dp
                        )
                    )
                }
            }

            Text(
                text = "Published · ${campsite.address}",
                fontSize = 12.sp,
                color = SubText
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Text(
                    text = "1 active lottery",
                    fontSize = 11.sp,
                    color = SubText
                )

                Text(
                    text = "${campsite.maxOccupancy} reservations",
                    fontSize = 11.sp,
                    color = SubText
                )
            }
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            number = "(Number)", // Edit these later
            label = "Waiting entrants",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            number = "(Number)",
            label = "Upcoming stays",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    number: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(74.dp),
        color = Color.White,
        shape = RoundedCornerShape(17.dp),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        // Has the two bits of text on it
        Column(
            modifier = Modifier.padding(
                horizontal = 13.dp,
                vertical = 11.dp
            ),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = number,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = HeaderColor
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = SubText
            )
        }
    }
}

@Composable
private fun AttentionCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    // Can be clicked because it has the arrow thingy
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick() // clicky
            },

        color = Color.White,
        shape = RoundedCornerShape(17.dp),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 15.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // card text
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HeaderColor
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = SubText
                )
            }

            // the right arrow icon
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "View details",
                tint = HeaderColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
