package com.example.lambda_aaa.model

import com.example.lambda_aaa.model.detail.NotificationDataSource
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.tasks.await

class NotificationRepository : NotificationDataSource {
    private val db = Firebase.firestore
    private val notificationsRef = db.collection("notifications")

    private fun DocumentSnapshot.campsiteIds(): List<String> =
        (get("campsiteIDs") as? List<*>)?.filterIsInstance<String>() ?: listOfNotNull(getString("campsiteID"))

    suspend fun sentBy(ownerId: String): List<AppNotification> =
        notificationsRef.whereEqualTo("senderId", ownerId).get().await().toObjects()

    suspend fun sendWaitlistBroadcast(ownerId: String, lotteryId: String, message: String, broadcastId: String): Int {
        require(ownerId.isNotBlank() && message.isNotBlank())
        val lotteryRef = db.collection("lotteries").document(lotteryId)
        val lottery = lotteryRef.get().await()
        val campsiteIds = lottery.campsiteIds()
        val campsite = campsiteIds.map { db.collection("campsites").document(it) }
            .firstOrNull { it.get().await().getString("ownerId") == ownerId }
        requireNotNull(campsite) { "Only the campsite owner can notify this waitlist." }

        val entries = db.collection("lottery_entries").whereEqualTo("lotteryId", lotteryId)
            .get().await().documents.filter { it.getString("status") == EntryStatus.WAITING.name }
            .groupBy { it.getString("userId").orEmpty() }
        var delivered = 0
        for ((uid, records) in entries) {
            if (uid.isBlank()) continue
            val delivery = notificationsRef.document("${broadcastId}_${uid}")
            val sent = db.runTransaction { transaction ->
                if (transaction.get(delivery).exists()) return@runTransaction true
                val currentLottery = transaction.get(lotteryRef)
                check(campsite.id in currentLottery.campsiteIds() &&
                    transaction.get(campsite).getString("ownerId") == ownerId) { "Lottery ownership changed." }
                val waiting = records.any {
                    val entry = transaction.get(it.reference)
                    entry.getString("status") == EntryStatus.WAITING.name &&
                        entry.getString("userId") == uid && entry.getString("lotteryId") == lotteryId
                }
                val user = transaction.get(db.collection("users").document(uid))
                if (!waiting || !user.exists() || user.getBoolean("notificationsEnabled") == false) {
                    return@runTransaction false
                }
                val notification = AppNotification(
                    recipientId = uid, senderId = ownerId, lotteryId = lotteryId, message = message.trim(),
                    type = NotificationType.OWNER_BROADCAST, broadcastId = broadcastId,
                    lotteryName = currentLottery.getString("name").orEmpty(), sendeeUid = uid,
                    sendeeName = user.getString("name").orEmpty(), senderUid = ownerId
                )
                transaction.set(delivery, notification.toMap())
                true
            }.await()
            if (sent) delivered++
        }
        return delivered
    }

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

    /**
     * Retrieve every notification log
     */
    override fun getAllNotifications(onResult: (Boolean, List<AppNotification>, String?) -> Unit) {
        notificationsRef.get()
            .addOnSuccessListener { documentSnapshots ->
                onResult(true, documentSnapshots.toObjects<AppNotification>(), null)
            }
            .addOnFailureListener { e -> onResult(false, listOf(), e.message) }
    }
}
