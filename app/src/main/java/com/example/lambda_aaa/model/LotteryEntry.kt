package com.example.lambda_aaa.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.UUID

data class LotteryEntry(
    @DocumentId val id: String = UUID.randomUUID().toString(),
    var lotteryId: String = "",
    var userId: String = "",
    var status: EntryStatus = EntryStatus.WAITING,
    @ServerTimestamp var timestamp: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "lotteryId" to lotteryId,
        "userId" to userId,
        "status" to status.name,
        "timestamp" to timestamp
    )
}
