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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lambda_aaa.R
import com.example.lambda_aaa.controller.NotificationController
import com.example.lambda_aaa.model.AppNotification
import com.example.lambda_aaa.model.NotificationType
import com.example.lambda_aaa.model.NotificationRepository
import kotlinx.coroutines.CancellationException
import java.text.DateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
        /**
         * Displays entrants notification inbox
         * Loads notifications for the user on start
         * Filters for lottery updates
         * Handles marking notifications as read
         * Collaborators: NotificationController, AppNotification, NotificationCard
         */
fun NotificationView(
    userId: String,
    notificationController: NotificationController = remember { NotificationController() },
    onBack: () -> Unit,
    onWinnerClick: (String) -> Unit = {},
    showSent: Boolean = false
) {
    if (showSent) {
        SentNotifications(userId, onBack)
        return
    }
    var notifications by remember { mutableStateOf<List<AppNotification>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        notificationController.loadNotifications(userId) { list ->
            notifications = list.filter {
                // filter for all type of messages
                it.type == NotificationType.OWNER_BROADCAST || it.type == NotificationType.WINNER || it.type == NotificationType.NOT_SELECTED || it.type == NotificationType.OWNER_MESSAGE || it.type == NotificationType.ENTRANT_MESSAGE
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

/**
 * Displays individual notifications in the inbox
 * Changes header based on read and win or non-selection
 * Collaborators: AppNotification, NotificationType
 */
@Composable
private fun NotificationCard(
    notification: AppNotification,
    onClick: () -> Unit
) {
    val isWinner = notification.type == NotificationType.WINNER
    val cardColor = if (notification.isRead) Color.White else FilterSelectColor

    val isMessage = notification.type == NotificationType.OWNER_MESSAGE ||
                    notification.type == NotificationType.ENTRANT_MESSAGE

    var displayMessage = when (notification.type) {
        NotificationType.WINNER -> "You were selected! Choose an available date before booking."
        NotificationType.NOT_SELECTED -> "You were not selected."
        NotificationType.OWNER_BROADCAST -> "${notification.lotteryName.ifBlank { notification.lotteryId }} · ${notification.message}"
        NotificationType.OWNER_MESSAGE, NotificationType.ENTRANT_MESSAGE -> notification.message
        else -> ""
    }
    // if notification belongs to message, also include information on sender and related lottery
    if (isMessage) {
        displayMessage = "Message from ${notification.senderName.ifBlank { "unknown" }} (ID: ${notification.senderUid.ifBlank { "unknown uid" }})" +
        (if (notification.lotteryName.isNotBlank()) " · ${notification.lotteryName}" else "") +
        ": " + displayMessage
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

internal fun groupSentNotifications(records: List<AppNotification>): List<List<AppNotification>> =
    records.sortedByDescending { it.timestamp }
        .groupBy { it.broadcastId.ifBlank { it.id } }.values.toList()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SentNotifications(ownerId: String, onBack: () -> Unit) {
    val repo = remember { NotificationRepository() }
    var groups by remember(ownerId) { mutableStateOf<List<List<AppNotification>>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var attempt by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<List<AppNotification>?>(null) }

    LaunchedEffect(ownerId, attempt) {
        loading = true
        error = null
        try {
            groups = groupSentNotifications(repo.sentBy(ownerId))
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (e: Exception) {
            error = e.localizedMessage ?: "Could not load sent notifications."
        } finally {
            loading = false
        }
    }
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text("Sent notifications") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
        })
    }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            when {
                loading -> item { CircularProgressIndicator() }
                error != null -> item {
                    Text(error.orEmpty())
                    TextButton(onClick = { attempt++ }) { Text("Retry") }
                }
                groups.isEmpty() -> item { Text("No sent notifications yet.") }
                else -> items(groups) { group ->
                    val first = group.first()
                    Card(onClick = { selected = group }, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(first.lotteryName.ifBlank { first.lotteryId }, fontWeight = FontWeight.Bold)
                            Text(first.message)
                            val recipient = if (group.size == 1) first.sendeeName.ifBlank { first.recipientId }
                                else "${group.size} recipients"
                            val date = first.timestamp?.toDate()?.let { DateFormat.getDateTimeInstance().format(it) }
                            Text("To: $recipient · ${date ?: "Time unavailable"}")
                        }
                    }
                }
            }
        }
    }
    selected?.let { group ->
        AlertDialog(onDismissRequest = { selected = null },
            title = { Text(group.first().lotteryName.ifBlank { group.first().lotteryId }) },
            text = {
                LazyColumn {
                    item { Text(group.first().message); Text("Recipients", fontWeight = FontWeight.Bold) }
                    items(group) { Text(it.sendeeName.ifBlank { it.recipientId }) }
                }
            },
            confirmButton = { TextButton(onClick = { selected = null }) { Text("Close") } })
    }
}
