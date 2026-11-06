package com.example.lambda_aaa.model.detail

import com.example.lambda_aaa.model.AppNotification

interface NotificationDataSource {
    /**
     * Saves all notifications for a specific user (recipientId)
     * Collaborators: AppNotification
     */
    fun saveNotification(notification: AppNotification, onResult: (Boolean, String?) -> Unit)

    /**
     * Fetches all notifications for a specific user (recipientId)
     * Collaborators: AppNotification
     */
    fun getNotifications(recipientId: String, onResult: (Boolean, List<AppNotification>, String?) -> Unit)

    /**
     * Updates a notification to isRead on click
     * Collaborators: NotificationDataSource, NotificationRepository
     */
    fun markAsRead(notificationId: String, onResult: (Boolean, String?) -> Unit)
}