package com.example.lambda_aaa.model
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.UUID

data class AppNotification(
    @DocumentId val id: String = UUID.randomUUID().toString(),
    var recipientId: String = "",
    var senderId: String = "",
    var lotteryId: String = "",
    var message: String = "",
    var type: NotificationType = NotificationType.WINNER,
    @get:PropertyName("isRead") @set:PropertyName("isRead") var isRead: Boolean = false,
    var senderName: String = "",    // sender name, for entrant/owner messaging system
    var senderUid: String = "",     // for admin
    var sendeeName: String = "",    // sendee name, for admin message log
    var sendeeUid: String = "",     // for admin
    var lotteryName: String = "",   // for message
    @ServerTimestamp var timestamp: Timestamp? = null,
    var broadcastId: String = "",
) {
    /**
     * Serializes AppNotification into Map<String, Any?> so that it can be stored in Firestore
     * Collaborators: NotificationType, Timestamp, Firestore
     */
    fun toMap(): Map<String, Any?> = mapOf(
        "recipientId" to recipientId,
        "senderId" to senderId,
        "lotteryId" to lotteryId,
        "message" to message,
        "type" to type.name,
        "isRead" to isRead,
        "senderName" to senderName,
        "senderUid" to senderUid,
        "sendeeName" to sendeeName,
        "sendeeUid" to sendeeUid,
        "lotteryName" to lotteryName,
        "timestamp" to (timestamp ?: FieldValue.serverTimestamp()),
        "broadcastId" to broadcastId,
    )
}
