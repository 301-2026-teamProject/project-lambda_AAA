package com.example.lambda_aaa.model.detail

import com.example.lambda_aaa.model.AppNotification

interface NotificationDataSource {
    fun saveNotification(notification: AppNotification, onResult: (Boolean, String?) -> Unit)
    fun getNotifications(recipientId: String, onResult: (Boolean, List<AppNotification>, String?) -> Unit)
    fun markAsRead(notificationId: String, onResult: (Boolean, String?) -> Unit)
}