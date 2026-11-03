package com.example.lambda_aaa.model
import com.google.firebase.Timestamp
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
    var isRead: Boolean = false,
    @ServerTimestamp var timestamp: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "recipientId" to recipientId,
        "senderId" to senderId,
        "lotteryId" to lotteryId,
        "message" to message,
        "type" to type.name,
        "isRead" to isRead,
        "timestamp" to timestamp
    )
}
