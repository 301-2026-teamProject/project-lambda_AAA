/**
 * UI implementation for the Entrant Profile Screen shown in the GUI mockup storyboards
 */

package com.example.lambda_aaa.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.lambda_aaa.controller.ProfileController

/**
 * Implements the Entrant Profile Screen according to the GUI mockup on GitHub.
 *
 * @param entrantProfileNavController the nav controller that handles navigation into and out of
 * EntrantProfileView
 * @param profileControl the profile controller that manages the current user profile
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntrantProfileView(
    entrantProfileNavController: NavController,
    profileControl : ProfileController
) {

    var name by remember { mutableStateOf(profileControl.getUserName()) }
    var email by remember { mutableStateOf(profileControl.getUserEmail()) }
    var phoneNumber by remember { mutableStateOf(profileControl.getUserPhone()) }
    var currentImageUrl by remember { mutableStateOf(profileControl.getUserPhoto()) }
    var notify by remember { mutableStateOf(true) }
    var changeImage by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }
    var showPolicies by remember { mutableStateOf(false) }


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

                // Code for profile screen starts here
                Column (modifier = Modifier.fillMaxSize().padding(8.dp)) {

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Profile and Settings",
                        color = Color(0xFF06402B),
                        fontSize = 24.sp)

                    HorizontalDivider(
                        thickness = 4.dp,
                        color = Color(0xFF06402B))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Spacer(modifier = Modifier.height(16.dp))

                        AsyncImage(model = profileControl.getUserPhoto(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(200.dp).clickable(onClick = {
                                changeImage = !changeImage
                            }))

                        Spacer(modifier = Modifier.height(16.dp))

                        // Users can change profile pictures by tapping the image which shows
                        // a new text field for them to input a new url
                        if (changeImage) {

                            OutlinedTextField(
                                value = currentImageUrl,
                                onValueChange = { currentImageUrl = it },
                                label = { Text("Input new image url")},
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    profileControl.updateProfile(newPhoto = currentImageUrl)
                                    changeImage = !changeImage
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF06402B), contentColor = Color.White),
                                shape = RectangleShape
                            ) {Text("Change Image")}


                        }

                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Display user's Name, Email, Phone
                    Text(text = "Name", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White
                        )
                    )
                    Text(text = "Email", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White
                        )
                    )
                    Text(text = "Phone", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White
                        )
                    )

                    Row (modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically) {

                        Text("Notifications", fontSize = 12.sp)

                        Spacer(modifier = Modifier.width(16.dp))

                        // Notifications button can be toggled on and off, button shown changes
                        // depending on state
                        if (notify) {
                            Button(
                                onClick = {
                                    profileControl.toggleUserNotifications()
                                    notify = !notify
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF06402B), contentColor = Color.White),
                                shape = RectangleShape
                            ) { Text("ON") }
                        }
                        else {
                            Button(
                                onClick = {
                                    profileControl.toggleUserNotifications()
                                    notify = !notify
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red, contentColor = Color.White),
                                shape = RectangleShape
                            ) { Text("OFF") }
                        }
                    }

                    TextButton(onClick = {showPolicies = !showPolicies}) {
                        Text(
                            "Policies and guidelines",
                            modifier = Modifier
                        )
                    }

                    if (showPolicies) {

                        HorizontalDivider(color = BorderColor, thickness = 2.dp)
                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            "This is the app's policies and guidelines.",
                            modifier = Modifier
                        )

                        Spacer(modifier = Modifier.height(5.dp))
                        HorizontalDivider(color = BorderColor, thickness = 2.dp)

                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row {

                        // Saves what is currently in the text fields as the user's new profile information
                        Button(
                            onClick = {
                                profileControl.updateProfile(name, email, phoneNumber)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF06402B), contentColor = Color.White),
                            shape = RectangleShape
                        ) { Text("SAVE PROFILE") }

                        Button(
                            onClick = {
                                confirmDelete = !confirmDelete
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent, contentColor = Color.Red),
                            shape = RectangleShape
                        ) { Text("DELETE PROFILE") }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Button to go back to previous screen
                    Button(
                        onClick = {
                            entrantProfileNavController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF06402B), contentColor = Color.White),
                        shape = RectangleShape
                    ) {Text("Back")}

                }
            }
        }
    }

    if (confirmDelete) {

        val closePrompt = {confirmDelete = false}

        AlertDialog(
            onDismissRequest = { closePrompt() },
            title = { Text("Confirm Delete Profile") },
            text = { Text("Are you sure you want to delete your profile?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        profileControl.deleteProfile()
                        closePrompt()
                    },
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        closePrompt()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )

    }

}
