package com.example.lambda_aaa.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.lambda_aaa.model.AppNotification
import com.example.lambda_aaa.model.NotificationRepository
import com.example.lambda_aaa.model.detail.NotificationDataSource

/**
 * Controller for admin; load every notification in database
 *
 * @property logs store logs for notifications
 * @property notificationDataSource manage repository and database interaction * @property ratings ratings related to the campsite of given cid
 * @property isLoading true while contacts are loading
 * @property errorMessage error text if loading failed; null otherwise
 * @property failed true if notification failed to be sent, false otherwise
 */
class AdminNotificationController(
    private val notificationDataSource: NotificationDataSource = NotificationRepository()
) {
    var logs by mutableStateOf<List<AppNotification>>(emptyList())
        private set
    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var failed by mutableStateOf(false)
        private set

    init {
        refresh()
    }

    fun refresh() {
        isLoading = true
        errorMessage = null
        notificationDataSource.getAllNotifications { success, logs, msg ->
            if (success) {
                this.logs = logs
            } else {
                errorMessage = "Failed to load notification logs: $msg"
                failed = true
            }
            isLoading = false
        }
    }
}