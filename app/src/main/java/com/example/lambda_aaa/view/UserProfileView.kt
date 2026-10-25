package com.example.lambda_aaa.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lambda_aaa.model.UserProfile

@Composable
fun UserProfileScreen(navController: NavController, user: UserProfile) {

    var name by remember { mutableStateOf(user.userName) }
    var email by remember { mutableStateOf(user.userEmail) }
    var phoneNumber by remember { mutableStateOf(user.userPhone) }
    var notify by remember { mutableStateOf(true) }

    Column (modifier = Modifier.fillMaxSize().padding(16.dp),) {

        Text(
            text = "Profile and Settings",
            color = Color.Green,
            fontSize = 24.sp)

        HorizontalDivider(
            thickness = 4.dp,
            color = Color.Green)

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

        Row () {
            Text("Notifications", fontSize = 12.sp)

            if (notify) {
                Button(
                    onClick = {
                        user.toggleNotifications()
                        notify = !notify
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green, contentColor = Color.White)
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
                    containerColor = Color.Green, contentColor = Color.White)
            ) { Text("SAVE PROFILE") }

            Button(
                onClick = {
                    // do something
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White, contentColor = Color.Red)
            ) { Text("DELETE PROFILE") }
        }

    }
}