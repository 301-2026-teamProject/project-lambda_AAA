package com.example.lambda_aaa.controller
import com.example.lambda_aaa.model.AppNotification
import com.example.lambda_aaa.model.NotificationRepository
import com.example.lambda_aaa.model.NotificationType

class NotificationController(
    private val notificationRepository: NotificationRepository = NotificationRepository()
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
        notificationRepository.saveNotification(notification, onResult)
    }
    fun loadNotifications(recipientId: String, onResult: (List<AppNotification>) -> Unit) {
        notificationRepository.getNotifications(recipientId) { success, list, _ ->
            if (success) {
                onResult(list)
            } else {
                onResult(emptyList())
            }
        }
    }
}
