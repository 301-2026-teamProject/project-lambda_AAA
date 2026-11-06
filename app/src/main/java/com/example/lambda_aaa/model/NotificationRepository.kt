package com.example.lambda_aaa.model
import com.example.lambda_aaa.model.detail.NotificationDataSource
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class NotificationRepository : NotificationDataSource {
    private val db = Firebase.firestore
    private val notificationsRef = db.collection("notifications")
    /**
     * Saves a notification to Firestore so it can be delivered to the receiver
     * Collaborators: AppNotification, Firestore
     */
    override fun saveNotification(notification: AppNotification, onResult: (Boolean, String?) -> Unit) {
        notificationsRef.document(notification.id).set(notification.toMap())
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    /**
     * Fetches all notifications to a specific user (recipientId) to display at the inbox
     * Firestore, AppNotification
     */
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

    /**
     * Updates a notification to isRead
     * Collaborators: Firebase
     */
    override fun markAsRead(notificationId: String, onResult: (Boolean, String?) -> Unit) {
        notificationsRef.document(notificationId).update("isRead", true)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
}
