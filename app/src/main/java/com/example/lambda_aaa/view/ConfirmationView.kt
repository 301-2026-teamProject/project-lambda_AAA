package com.example.lambda_aaa.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lambda_aaa.R
import com.example.lambda_aaa.controller.CampsiteManagementController
import com.example.lambda_aaa.controller.ProfileController
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.CampsiteRepository
import com.example.lambda_aaa.model.UserProfile
import com.example.lambda_aaa.model.UserRepository

@Composable
fun ConfirmationView(
    navController: NavController,
    inputType: String,
    inputID: String,
    campsiteRepo: CampsiteRepository,
    userRepo: UserRepository,
    onBack: () -> Unit = {},
    ) {

    var inputName by remember { mutableStateOf( "No Name Found") }
    var adminReason by remember { mutableStateOf( "$inputType violates promotional-content policy.") }
    var inputCampsite: Campsite? = null
    var inputUser: UserProfile? = null
    val localContext = LocalContext.current

    when (inputType) {
        "Campsite" -> {
            campsiteRepo.getCampsite(inputID) { boolCheck, campsite, _ ->
                if (boolCheck) {
                    inputCampsite = campsite
                    inputName = campsite!!.name
                }
            }
        }
        "Profile" -> {
            userRepo.getProfile(inputID) { boolCheck, profile, _ ->
                if (boolCheck) {
                    inputUser = profile
                    inputName = profile!!.name
                }
            }
        }
        else -> {
            onBack()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)

    ) {
        TopBar(
            onBack = onBack,
            browseOption = inputType,
            onExtra = {}
        )

        // This Column encapsulates the rest of the Screen
        // Also the ENTIRE THING is scrollable so it can be used horizontally as well :)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                Surface(shape = RoundedCornerShape(18.dp), color = Background) {
                    Box(Modifier
                        .fillMaxWidth()
                        .heightIn(min = 180.dp)) {
                        val fallback = painterResource(R.drawable.campsite_placeholder)
                        AsyncImage(
                            model = R.drawable.campsite_placeholder,
                            contentDescription = "Doesn't matter",
                            placeholder = fallback,
                            fallback = fallback,
                            error = fallback,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize(),
                        )

                        Column(
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color(0xDD243328)
                                        )
                                    )
                                )
                                .padding(top = 36.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text("Temp", color = Color.White, style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold, modifier = Modifier.semantics { heading() })
                        }
                    }
                }

                // Displays all the filters (to be fixed soon)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = inputName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HeaderColor
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Placeholder",
                            fontSize = 12.sp,
                            color = SubText
                        )
                    }

                    // Report count badge
                    Surface(
                        shape = CircleShape,
                        color = BorderColor
                    ) {
                        Text(
                            text = "placeholder",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),
                            fontSize = 11.sp,
                            color = SelectColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // USED FOR WHEN WE HAVE PROPER REVIEWS FOR EACH CAMPSITE
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("1").forEach { _ ->
                        ReportSummaryCard(
                            summary = "Placeholder"
                        )
                    }
                    Surface(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(41.dp),
                        shape = RoundedCornerShape(13.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Remove Report (Doesn't work)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = HeaderColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Administrative reason",
                    fontSize = 12.sp,
                    color = SubText
                )

                // Editable admin remove notice
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(82.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderColor)
                ) {

                    BasicTextField(
                        value = adminReason,
                        onValueChange =  {adminReason = it},
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 12.sp,
                            color = HeaderColor,
                            lineHeight = 19.sp
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(11.dp),
                        singleLine = false
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = BorderColor
                ) {
                    Text(
                        text = "Before removing: Review active lotteries and reservations affected by this campsite.",
                        fontSize = 12.sp,
                        lineHeight = 21.sp,
                        color = HeaderColor,
                        modifier = Modifier.padding(
                            horizontal = 13.dp,
                            vertical = 11.dp
                        )
                    )
                }

                if (inputType == "Profile") {

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(41.dp),
                        shape = RoundedCornerShape(13.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = "Ban Profile",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = HeaderColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    onClick = {
                        when (inputType) {
                            "Campsite" -> {
                                val campsiteController = CampsiteManagementController(cid = inputCampsite!!.cid, uid = inputCampsite!!.ownerId, isNew = false, repository = campsiteRepo, context = localContext)
                                campsiteController.deleteCampsite(inputCampsite!!) { message -> }
                                onBack()
                            }
                            "Profile" -> {
                                val userController = ProfileController(inputUser!!)
                                userController.deleteProfile(localContext)
                                onBack()
                            }
                            else -> {
                                onBack()
                            }
                        }


                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(41.dp),
                    shape = RoundedCornerShape(13.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "Delete $inputType",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = HeaderColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

    }
}

@Composable
private fun TopBar(
    onBack: () -> Unit,
    browseOption: String,
    onExtra: () -> Unit
) {

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Color.White)
                .padding(horizontal = 16.dp)
        ) {
            // Back button
            Surface(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterStart),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = HeaderColor,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            // Centered title
            Text(
                text = "Review $browseOption",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = HeaderColor,
                modifier = Modifier.align(Alignment.Center)
            )

            // Dropdown menu for searchable results
            Box(modifier = Modifier.align(Alignment.CenterEnd) ) {
                // This part is just the textbox that you click to open the list of options
                Surface(
                    onClick = onExtra,
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MoreHoriz,
                            contentDescription = "Filter",
                            tint = HeaderColor,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }
        }
        // Bottom divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BorderColor)
        )
    }
}

@Composable
private fun ReportSummaryCard( summary: String ) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke( 1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 13.dp
            )
        ) {

            Text(
                text = "Report summary",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = HeaderColor
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = summary,
                fontSize = 12.sp,
                color = SubText
            )
        }
    }
}