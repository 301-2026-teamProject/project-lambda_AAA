package com.example.lambda_aaa.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lambda_aaa.R
import com.example.lambda_aaa.controller.NotificationController
import com.example.lambda_aaa.model.AppNotification
import com.example.lambda_aaa.model.NotificationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationView(
    userId: String,
    notificationController: NotificationController = remember { NotificationController() },
    onBack: () -> Unit,
    onWinnerClick: (String) -> Unit = {}
) {
    var notifications by remember { mutableStateOf<List<AppNotification>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        notificationController.loadNotifications(userId) { list ->
            notifications = list.filter { 
                it.type == NotificationType.WINNER || it.type == NotificationType.NOT_SELECTED 
            }.sortedByDescending { it.timestamp?.toDate() }
            isLoading = false
        }
    }
    Scaffold(
        containerColor = Background,
        contentColor = HeaderColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notifications", style = MaterialTheme.typography.titleMedium) },
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
            isLoading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator()
            }
            notifications.isEmpty() -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text("No notifications yet.")
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications) { notification ->
                    NotificationCard(
                        notification = notification,
                        onClick = {
                            if (!notification.isRead) {
                                notificationController.markAsRead(notification.id) { success, _ ->
                                    if (success) {
                                        notifications = notifications.map {
                                            if (it.id == notification.id) it.copy(isRead = true) else it
                                        }
                                    }
                                }
                            }
                            if (notification.type == NotificationType.WINNER) {
                                onWinnerClick(notification.lotteryId)
                            }
                        }
                    )
                }
            }
        }
    }
}
@Composable
private fun NotificationCard(
    notification: AppNotification,
    onClick: () -> Unit
) {
    val isWinner = notification.type == NotificationType.WINNER
    val cardColor = if (notification.isRead) Color.White else FilterSelectColor

    val displayMessage = when (notification.type) {
        NotificationType.WINNER -> "You were selected! Choose an available date before booking."
        NotificationType.NOT_SELECTED -> "You were not selected."
        else -> ""
    }
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = cardColor,
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
                        NotificationType.WINNER -> "WINNER"
                        NotificationType.NOT_SELECTED -> "RESULT"
                        else -> "Notification"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
                Text(
                    text = displayMessage,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (!notification.isRead) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF3D5A40),
                    contentColor = Color.White
                ) {
                    Text(
                        text = "New",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
