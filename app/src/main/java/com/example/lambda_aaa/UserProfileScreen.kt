package com.example.lambda_aaa

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UserProfileScreen() {
    Column (modifier = Modifier.fillMaxSize().padding(16.dp),) {

        Text(
            text = "Profile and Settings",
            color = Color.Green,
            fontSize = 24.sp)

        HorizontalDivider(
            thickness = 4.dp,
            color = Color.Green)

        Text(text = "Name", fontSize = 12.sp)

    }
}