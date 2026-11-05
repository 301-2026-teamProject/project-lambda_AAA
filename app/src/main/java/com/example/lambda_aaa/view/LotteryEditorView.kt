/**
 * UI implementation for the Lottery Editor Screen shown in the GUI mockup storyboards
 */

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.navigation.NavController

// Colors used to color buttons, text, and background
private val Background = Color(0xFFF3F0E9) // Used for the background and Campsite icon. Bone-White color.
private val BorderColor = Color(0xFFDEDCD4) // Used for borders. A muted equivalent of the Background color.
private val HeaderColor = Color(0xFF242823) // Used for Headers. Muted Black.
private val SubText = Color(0xFF6E716C) // Used for smaller text and Icons. Dark Gray.
private val AddGreen = Color(0xFF315E43) // The big green add button

/**
 * Implements the Lottery Editor Screen according to the GUI mockups on the Wiki page in GitHub.
 *
 * @param lotteryEditorNavController the nav controller that handles navigation into and out of
 * LotteryEditorView
 */
@Composable
fun LotteryEditorView(
    lotteryEditorNavController: NavController
) {

    var expanded by remember { mutableStateOf(false) }
    var icon by remember { mutableStateOf(Icons.Filled.KeyboardArrowDown)}
    var lotteryName by remember { mutableStateOf("") }

    icon = if (expanded) {
        Icons.Filled.KeyboardArrowUp
    }
    else {
        Icons.Filled.KeyboardArrowDown
    }

    Scaffold(
        containerColor = Background,
    ) { innerPadding  ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Background)
        ) {
            HorizontalDivider(color = BorderColor, thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .background(Color.White)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.size(40.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Notifications",
                            tint = HeaderColor,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Create Lottery",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(ButtonDefaults.MinWidth))

            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp, bottom = 20.dp),

                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Campsite",
                    fontSize = 13.sp,
                    color = SubText,
                    fontWeight = FontWeight.Medium,
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedTextField(
                        value = "Pine Ridge",
                        onValueChange = {},
                        trailingIcon = {
                            IconButton(onClick = {expanded = !expanded}) {
                                Icon(imageVector = icon,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }

                Text(
                    text = "Lottery Name",
                    fontSize = 13.sp,
                    color = SubText,
                    fontWeight = FontWeight.Medium,
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedTextField(
                        value = lotteryName,
                        onValueChange = { lotteryName = it},
                    )
                }

                ThreeByThreeFields()

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AddGreen,
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        )
                    ) { Text("Review Lottery")}
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) { Text("Save Draft")}
                }

            }
        }
    }
}

/**
 * Helper function to display the 3x3 inputs fields in the Lottery Editor Screen UI
 */
@Composable
fun ThreeByThreeFields() {

    var registerOpenDate by remember { mutableStateOf("") }
    var registerCloseDate by remember { mutableStateOf("") }
    var bookingStartDate by remember { mutableStateOf("") }
    var bookingEndDate by remember { mutableStateOf("") }
    var numWinners by remember { mutableStateOf("") }
    var waitlistCapacity by remember { mutableStateOf("") }

    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Registration opens",
            fontSize = 13.sp,
            color = SubText,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.width(80.dp))

        Text(
            text = "Registration closes",
            fontSize = 13.sp,
            color = SubText,
            fontWeight = FontWeight.Medium,
        )
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth(0.49f),
        ) {
            OutlinedTextField(
                value = registerOpenDate,
                onValueChange = {registerOpenDate = it},
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null
                    )
                }
            )
        }

        Spacer(modifier = Modifier.width((10.dp)))

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth(1f),
        ) {
            OutlinedTextField(
                value = registerCloseDate,
                onValueChange = {registerCloseDate = it},
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null
                    )
                }
            )
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Booking starts",
            fontSize = 13.sp,
            color = SubText,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.width(105.dp))

        Text(
            text = "Booking ends",
            fontSize = 13.sp,
            color = SubText,
            fontWeight = FontWeight.Medium,
        )
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth(0.49f),
        ) {
            OutlinedTextField(
                value = bookingStartDate,
                onValueChange = {bookingStartDate = it},
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null
                    )
                }
            )
        }

        Spacer(modifier = Modifier.width((10.dp)))

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth(1f),
        ) {
            OutlinedTextField(
                value = bookingEndDate,
                onValueChange = {bookingEndDate = it},
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null
                    )
                }
            )
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Number of winners",
            fontSize = 13.sp,
            color = SubText,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.width(80.dp))

        Text(
            text = "Waitlist capacity",
            fontSize = 13.sp,
            color = SubText,
            fontWeight = FontWeight.Medium,
        )
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth(0.49f),
        ) {
            OutlinedTextField(
                value = numWinners,
                onValueChange = {numWinners = it},
            )
        }

        Spacer(modifier = Modifier.width((10.dp)))

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth(1f),
        ) {
            OutlinedTextField(
                value = waitlistCapacity,
                onValueChange = {waitlistCapacity = it}
            )
        }
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = TrailSage,
        contentColor = TrailInk,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            "Capacity check: $numWinners from a maximum of $waitlistCapacity entrants.",
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            modifier = Modifier.padding(14.dp)
        )
    }
}