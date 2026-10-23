package com.example.lambda_aaa.view

import android.R.id.message
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.lambda_aaa.controller.CampsiteDetailController
import com.example.lambda_aaa.model.Campsite
import kotlin.text.isNotBlank

@Composable
fun CampsiteOwnerView(
    uid: String,
    controller: CampsiteDetailController,
    onClickRegister: () -> Unit,  // clicking register lead user to register page
    onClickCampsite: (String) -> Unit,  // clicking an owned campsite lead user to edit page
    success: Boolean,
    message: String
) {
    CampsiteOwnerContent(
        success = success,
        campsiteEditMessage = message,
        isLoading = controller.isLoading,
        campsites = controller.campsites,
        errorMessage = controller.errorMessage,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampsiteOwnerContent(
    success: Boolean,
    campsiteEditMessage: String,
    isLoading: Boolean,
    campsites: List<Campsite>,
    errorMessage: String?,
) {
    TopAppBar(
        title = {
            Text("Owner Dashboard")
        }
    )
// the following should be inside CampsiteDetailView to inform campsite owner campsite
    // registration / modification status
//    if (campsiteEditMessage.isNotBlank()) {
//        if (success) {
//            Text(message, color = Color.Green)
//        } else {
//            Text(message, color = MaterialTheme.colorScheme.error)
//        }
//    }
}