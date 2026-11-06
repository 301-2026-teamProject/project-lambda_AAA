package com.example.lambda_aaa.controller
import com.example.lambda_aaa.model.AppNotification
import com.example.lambda_aaa.model.detail.NotificationDataSource
import com.example.lambda_aaa.model.NotificationRepository
import com.example.lambda_aaa.model.NotificationType

class NotificationController(
    private val notificationDataSource: NotificationDataSource = NotificationRepository()
) {
    /**
     * Constructs a new AppNotification instance and saves it
     * Collaborators: AppNotification, NotificationDataSource, NotificationRepository, NotificationType
     */
    fun sendNotification(
        recipientId: String,
        senderId: String,
        lotteryId: String,
        message: String,
        type: NotificationType,
        onResult: (Boolean, String?) -> Unit
    ) {
        val notification = AppNotification(
            recipientId = recipientId,
            senderId = senderId,
            lotteryId = lotteryId,
            message = message,
            type = type,
            isRead = false
        )
        notificationDataSource.saveNotification(notification, onResult)
    }

    /**
     * Fetches all notifications that are associated with a user (recipientId) and send the list to the UI
     * Collaborators: NotificationDataSource, NotificationRepository, AppNotification
     */
    fun loadNotifications(recipientId: String, onResult: (List<AppNotification>) -> Unit) {
        notificationDataSource.getNotifications(recipientId) { success, list, _ ->
            if (success) {
                onResult(list)
            } else {
                onResult(emptyList())
            }
        }
    }

    /**
     * Requests data source to update notification status to isRead = true when a user clicks on it
     * Collaborators: NotificationDataSource, NotificationRepository
     */
    fun markAsRead(notificationId: String, onResult: (Boolean, String?) -> Unit) {
        notificationDataSource.markAsRead(notificationId, onResult)
    }
}
