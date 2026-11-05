package com.example.lambda_aaa.controller
import com.example.lambda_aaa.model.AppNotification
import com.example.lambda_aaa.model.detail.NotificationDataSource
import com.example.lambda_aaa.model.NotificationRepository
import com.example.lambda_aaa.model.NotificationType

class NotificationController(
    private val notificationDataSource: NotificationDataSource = NotificationRepository()
) {
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
    fun loadNotifications(recipientId: String, onResult: (List<AppNotification>) -> Unit) {
        notificationDataSource.getNotifications(recipientId) { success, list, _ ->
            if (success) {
                onResult(list)
            } else {
                onResult(emptyList())
            }
        }
    }
    fun markAsRead(notificationId: String, onResult: (Boolean, String?) -> Unit) {
        notificationDataSource.markAsRead(notificationId, onResult)
    }
}
