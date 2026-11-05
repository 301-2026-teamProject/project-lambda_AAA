package com.example.lambda_aaa.model
import com.example.lambda_aaa.model.detail.NotificationDataSource
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class NotificationRepository : NotificationDataSource {
    private val db = Firebase.firestore
    private val notificationsRef = db.collection("notifications")
    override fun saveNotification(notification: AppNotification, onResult: (Boolean, String?) -> Unit) {
        notificationsRef.document(notification.id).set(notification.toMap())
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
    override fun getNotifications(recipientId: String, onResult: (Boolean, List<AppNotification>, String?) -> Unit) {
        notificationsRef.whereEqualTo("recipientId", recipientId)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.toObjects(AppNotification::class.java)
                onResult(true, list, null)
            }
            .addOnFailureListener { e ->
                onResult(false, emptyList(), e.message)
            }
    }
    override fun markAsRead(notificationId: String, onResult: (Boolean, String?) -> Unit) {
        notificationsRef.document(notificationId).update("isRead", true)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
}
