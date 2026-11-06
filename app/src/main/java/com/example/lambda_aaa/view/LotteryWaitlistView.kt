/**
 * Code for implementing the Lottery Waitlist Screen as show on the GUI mockup found in GitHub
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardBackspace
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Implements the UI from the GitHub GUI mockup of Lottery Waitlist Screen
 *
 * @param lotteryWaitlistNavController the controller that handles navigation into and out of
 * LotteryWaitlistView
 */
@Composable
fun LotteryWaitlistView(
    lotteryWaitlistNavController: NavController
) {

    Scaffold(
        containerColor = Background,
    ) { innerPadding ->
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
                            imageVector = Icons.AutoMirrored.Filled.KeyboardBackspace,
                            contentDescription = "Backspace",
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
                        "Lottery Waitlist",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.size(40.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "Triple Dots",
                            tint = HeaderColor,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "REGISTRATION ENDED",
                            fontSize = 13.sp,
                            color = SubText,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "17 entrants",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = FilterSelectColor,
                        contentColor = HeaderColor,
                        modifier = Modifier.fillMaxWidth(0.3f),
                    ) {
                        Text(
                            "5 winners",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = AddGreen,
                        border = BorderStroke(1.dp, BorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            )
                        ) { Text("Draw winners") }
                    }
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Entrants",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, BorderColor),
                        modifier = Modifier.fillMaxWidth(0.28f),
                    ) {
                        Text(
                            "All statuses",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )

                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))

                        Text("Notify waitlist")
                    }
                }

            }

        }
    }
}

/**
 * Helper function to extract the Entrant's initials for display
 *
 * @param name the name of the Entrant whose initials are to be extracted
 * @return the initials of the Entrant as a string of two capital characters
 */
fun ExtractInitials(name: String) : String {
    var cleanName = name.trim()
    val firstInitial = cleanName[0]
    var initials = ""
    cleanName = cleanName.substring(1)

    for (char in cleanName) {
        if (char.isUpperCase()) {
            initials = "$firstInitial$char"
        }
    }

    return initials
}