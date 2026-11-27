package com.example.lambda_aaa.view

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lambda_aaa.model.CampsiteRepository

@Composable
fun EntrantRatingView (
    onCancel: () -> Unit,
    campsiteId: String
) {
    val campsiteDB = remember { CampsiteRepository() }
    var campsiteName by remember { mutableStateOf("") }
    var userReview by remember { mutableStateOf("")}

    DisposableEffect(campsiteDB) {
        onDispose { campsiteDB.close() }
    }

    LaunchedEffect(Unit) {
        campsiteDB.getCampsite(campsiteId) {success, campsite, msg ->
            if (success) {
                campsiteName = campsite?.name ?: ""
            } else {
                Log.e("Campsite Review Error", "Unable to find campsite to review: $msg")
            }
        }

    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(innerPadding),
        ) {

            // This Column encapsulates the rest of the Screen
            // Also the ENTIRE THING is scrollable so it can be used horizontally as well :)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Text(
                        text = "ENTRANT · DISCOVER",
                        color = SubText,
                        fontSize = 10.sp,
                        letterSpacing = 0.75.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 20.dp),
                    )
                    Text(
                        text = "Find your next campsite",
                        color = HeaderColor,
                        fontSize = 16.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 3.dp),
                    )
                }

                // Code for Ratings Screen starts here
                Column (modifier = Modifier.fillMaxSize().padding(8.dp)) {

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Rate campsite",
                        color = Color(0xFF06402B),
                        fontSize = 24.sp
                    )

                    HorizontalDivider(
                        thickness = 4.dp,
                        color = Color(0xFF06402B)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(campsiteName, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    StarScore(modifier = Modifier)

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth().height(height = 140.dp),
                        value = userReview,
                        onValueChange = { userReview = it },
                        placeholder = {
                            Text(
                                "Leave feedback to improve the experience for others.",
                                color = SubText
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier) {

                        TextButton(
                            onClick = onCancel,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White, contentColor = Color.Black),
                            shape = RectangleShape,
                            border = BorderStroke(width = 1.5.dp, color = Color.Gray)
                        ) {
                            Text("CANCEL")
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        TextButton(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF246B45), contentColor = Color.White),
                            shape = RectangleShape,
                        ) {
                            Text("SUBMIT RATING")
                        }

                    }


                }


            }
        }

    }
}

@Composable
fun StarScore(modifier: Modifier) {
    Row(modifier = Modifier) {
        for (i in 0..<5) {
            IconButton(onClick = {}, modifier.size(50.dp)) {
                Icon(
                    imageVector = Icons.Outlined.StarOutline,
                    contentDescription = "Empty Star",
                    modifier.size(36.dp),
                    tint = Color(0xFFFFC107)
                )
            }
        }
    }
}