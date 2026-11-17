package com.example.lambda_aaa.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.lambda_aaa.model.MessageContact
import com.example.lambda_aaa.model.MessagingRepository
import com.example.lambda_aaa.model.NotificationType
import com.example.lambda_aaa.model.Role

/**
 * Loads the list of people the current user can message and sends messages to them.
 *
 * A message is stored as an AppNotification (type OWNER_MESSAGE or ENTRANT_MESSAGE), so it shows up
 * in the recipient's notification inbox. The view never touches a repository.
 *
 * @property uid user id of the sender (the signed-in user)
 * @property role OWNER lists entrants of the user's lotteries; ENTRANT lists owners of joined lotteries
 * @property repository resolves contacts from lotteries, entries, campsites and users
 * @property notifications used to persist the message as a notification
 * @property contacts people that can be messaged
 * @property isLoading true while contacts are loading
 * @property errorMessage error text if loading failed; null otherwise
 * @property isSending true while a message is being saved
 */
class MessagingController(
    private val uid: String,
    val role: Role,
    private val repository: MessagingRepository,
    private val notifications: NotificationController = NotificationController(),
) {
    var contacts by mutableStateOf<List<MessageContact>>(emptyList())
        private set
    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var isSending by mutableStateOf(false)
        private set

    private var senderName = ""

    init {
        repository.displayName(uid) { senderName = it.orEmpty() }
        refresh()
    }

    fun refresh() {
        isLoading = true
        errorMessage = null
        val handle: (Boolean, List<MessageContact>, String?) -> Unit = { success, list, msg ->
            if (success) contacts = list else errorMessage = "Failed to load contacts: $msg"
            isLoading = false
        }
        if (role == Role.OWNER) repository.ownerContacts(uid, handle)
        else repository.entrantContacts(uid, handle)
    }

    /**
     * Sends a message to a contact.
     *
     * @param contact the recipient (and the lottery the message is about)
     * @param text message body; blank messages are rejected
     * @param onResult (success, text suitable for a snackbar)
     */
    fun send(contact: MessageContact, text: String, onResult: (Boolean, String) -> Unit) {
        val body = text.trim()
        if (body.isEmpty()) {
            onResult(false, "Message cannot be empty.")
            return
        }
        if (isSending) return
        isSending = true
        notifications.sendNotification(
            recipientId = contact.uid,
            senderId = uid,
            lotteryId = contact.lotteryId,
            message = body,
            type = if (role == Role.OWNER) NotificationType.OWNER_MESSAGE else NotificationType.ENTRANT_MESSAGE,
            senderName = senderName.ifBlank { if (role == Role.OWNER) "Campsite owner" else "Entrant" },
            lotteryName = contact.lotteryName,
        ) { success, error ->
            isSending = false
            if (success) onResult(true, "Message sent to ${contact.name}.")
            else onResult(false, "Failed to send message: ${error ?: "unknown error"}")
        }
    }
}
