package com.example.lambda_aaa.view

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.lambda_aaa.model.UserProfile
import okhttp3.HttpUrl.Companion.toHttpUrl

@Composable
fun UserProfileView(navController: NavController, user: UserProfile) {

    var name by remember { mutableStateOf(user.userName) }
    var email by remember { mutableStateOf(user.userEmail) }
    var phoneNumber by remember { mutableStateOf(user.userPhone) }
    var notify by remember { mutableStateOf(true) }
    var changeImage by remember { mutableStateOf(false) }
    var currentImageUrl by remember { mutableStateOf(user.userPhoto) }

    Column (modifier = Modifier.fillMaxSize().padding(16.dp)) {

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

            AsyncImage(model = user.userPhoto,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(200.dp).clickable(onClick = {
                    changeImage = !changeImage
                }))

            Spacer(modifier = Modifier.height(16.dp))

            if (changeImage) {

                TextField(
                    value = currentImageUrl,
                    onValueChange = { currentImageUrl = it },
                    label = { Text("Input new image url")}
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        user.updatePhoto(currentImageUrl)
                        changeImage = !changeImage },
                    colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF06402B), contentColor = Color.White)
                ) {Text("Change Image")}


            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Name", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        TextField(
            value = name,
            onValueChange = { name = it }
        )
        Text(text = "Email", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        TextField(
            value = email,
            onValueChange = { email = it}
        )
        Text(text = "Phone", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it}
        )

        Row (modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically) {

            Text("Notifications", fontSize = 12.sp)

            Spacer(modifier = Modifier.width(16.dp))

            if (notify) {
                Button(
                    onClick = {
                        user.toggleNotifications()
                        notify = !notify
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF06402B), contentColor = Color.White)
                ) { Text("ON") }
            }
            else {
                Button(
                    onClick = {
                        user.toggleNotifications()
                        notify = !notify
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red, contentColor = Color.White)
                ) { Text("OFF") }
            }
        }

        Text("Policy and Guidelines >", fontSize = 12.sp)

        Row() {

            Button(
                onClick = {
                    user.updateName(name)
                    user.updateEmail(email)
                    user.updatePhone(phoneNumber)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF06402B), contentColor = Color.White)
            ) { Text("SAVE PROFILE") }

            Button(
                onClick = {
                    // After halfway point
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, contentColor = Color.Red)
            ) { Text("DELETE PROFILE") }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF06402B), contentColor = Color.White)
        ) {Text("Back to Dashboard")}

    }
}