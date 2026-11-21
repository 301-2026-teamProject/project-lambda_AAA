package com.example.lambda_aaa.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lambda_aaa.R
import com.example.lambda_aaa.controller.MessagingController
import com.example.lambda_aaa.model.MessageContact
import com.example.lambda_aaa.model.Role
import kotlinx.coroutines.launch

private const val MAX_MESSAGE_LENGTH = 500

/**
 * List of people the current user can message (entrants for an owner, owners for an entrant).
 * Tapping a card opens a dialog to write and send a message; the message arrives in the
 * recipient's notifications.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagingView(
    controller: MessagingController,
    onBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var selected by remember { mutableStateOf<MessageContact?>(null) }
    val isOwner = controller.role == Role.OWNER

    Scaffold(
        containerColor = Background,
        contentColor = HeaderColor,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (isOwner) "Message your Entrants" else "Message Campsite Owners",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = HeaderColor
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back), tint = HeaderColor)
                    }
                }
            )
        }
    ) { padding ->
        when {
            controller.isLoading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator()
            }

            controller.errorMessage != null -> Column(
                Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(controller.errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                Button(onClick = controller::refresh) { Text(stringResource(R.string.retry)) }
            }

            controller.contacts.isEmpty() -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text(
                    if (isOwner) "No entrants have joined your lotteries yet."
                    else "Join a lottery to message its campsite owner.",
                    color = SubText
                )
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(controller.contacts, key = { "${it.uid}:${it.lotteryId}" }) { contact ->
                    ContactCard(contact, onClick = { selected = contact })
                }
            }
        }
    }

    selected?.let { contact ->
        SendMessageDialog(
            contact = contact,
            isSending = controller.isSending,
            onDismiss = { selected = null },
            onSend = { text ->
                controller.send(contact, text) { success, message ->
                    scope.launch { snackbarHostState.showSnackbar(message) }
                    if (success) selected = null
                }
            }
        )
    }
}

@Composable
private fun ContactCard(contact: MessageContact, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        contentColor = HeaderColor,
        border = BorderStroke(1.dp, BorderColor),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(contact.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text("UID: ${contact.uid}", color = SubText, fontSize = 11.sp)
            val campsite = if (contact.campsiteName.isBlank()) "" else " · ${contact.campsiteName}"
            Text("Lottery: ${contact.lotteryName.ifBlank { "Unnamed lottery" }}$campsite",
                color = SubText, fontSize = 12.sp)
        }
    }
}

@Composable
private fun SendMessageDialog(
    contact: MessageContact,
    isSending: Boolean,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit,
) {
    var text by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { if (!isSending) onDismiss() },
        containerColor = Color.White,
        title = { Text("Message ${contact.name}") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { if (it.length <= MAX_MESSAGE_LENGTH) text = it },
                label = { Text("Message") },
                supportingText = { Text("${text.length}/$MAX_MESSAGE_LENGTH") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(onClick = { onSend(text) }, enabled = text.isNotBlank() && !isSending) {
                Text(if (isSending) "Sending" else "Send")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSending) { Text("Cancel") }
        }
    )
}