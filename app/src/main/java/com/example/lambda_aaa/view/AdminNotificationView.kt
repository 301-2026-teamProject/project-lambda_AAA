package com.example.lambda_aaa.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lambda_aaa.R
import com.example.lambda_aaa.controller.AdminNotificationController
import com.example.lambda_aaa.model.AppNotification
import com.example.lambda_aaa.model.NotificationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNotificationView(
    adminNotificationController: AdminNotificationController = remember { AdminNotificationController() },
    onBack: () -> Unit
) {
    val logs = adminNotificationController.logs
    val isLoading = adminNotificationController.isLoading
    val errorMessage = adminNotificationController.errorMessage
    val failed = adminNotificationController.failed

    Scaffold(
        containerColor = Background,
        contentColor = HeaderColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("View All Notification Logs", style = MaterialTheme.typography.titleMedium) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White, titleContentColor = HeaderColor),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back), tint = HeaderColor)
                    }
                }
            )
        }
    ) { padding ->
        when {
            failed -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text("Failed to load notification logs: $errorMessage")
            }
            isLoading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator()
            }
            logs.isEmpty() -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text("No logs yet.")
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(logs) { logs ->
                    NotificationCard(
                        notification = logs
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: AppNotification
) {
    val isWinner = notification.type == NotificationType.WINNER

    val isMessage = notification.type == NotificationType.OWNER_MESSAGE ||
            notification.type == NotificationType.ENTRANT_MESSAGE

    var displayMessage = when (notification.type) {
        NotificationType.WINNER -> "You were selected! Choose an available date before booking."
        NotificationType.NOT_SELECTED -> "You were not selected."
        NotificationType.OWNER_MESSAGE, NotificationType.ENTRANT_MESSAGE -> notification.message
        else -> ""
    }
    // if notification belongs to message, also include information on sender and related lottery
    if (isMessage) {
        displayMessage = "Message from ${notification.senderName.ifBlank { "unknown" }} (ID: ${notification.senderUid.ifBlank { "unknown sender uid" }}) to ${notification.sendeeName.ifBlank { "unknown" }} (ID: ${notification.sendeeUid.ifBlank { "unknown sendee uid" }})" +
                (if (notification.lotteryName.isNotBlank()) " · ${notification.lotteryName}" else "") +
                ": " + displayMessage
    }
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        contentColor = HeaderColor,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = if (isWinner) Color(0xFF3D5A40) else HeaderColor
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = when (notification.type) {
                        NotificationType.WINNER -> "${notification.timestamp}: WINNER"
                        NotificationType.NOT_SELECTED -> "${notification.timestamp}: RESULT"
                        NotificationType.OWNER_MESSAGE -> "${notification.timestamp}: Owner to Entrant Message"
                        NotificationType.ENTRANT_MESSAGE -> "${notification.timestamp}: Entrant to Owner Message"
                        else -> "${notification.timestamp}: Notification"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
                Text(
                    text = displayMessage,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}