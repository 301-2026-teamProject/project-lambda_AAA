package com.example.lambda_aaa.model
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class EntryRepository {
    private val db = Firebase.firestore
    private val entriesRef = db.collection("lottery_entries")

    /**
     * Allows an entrant (userId) to join a lottery waitlist (lotteryId)
     * Checks if user has already joined, and if they are WITHDRAWN to keep entrants under maxCapacity
     * Collaborators: EntryStatus, LotteryEntry, Firestore
     */
    fun joinWaitlist(
        lotteryId: String,
        userId: String,
        maxCapacity: Int,
        onResult: (Boolean, String?) -> Unit
    ) {
        entriesRef.whereEqualTo("lotteryId", lotteryId)
            .get()
            .addOnSuccessListener { lotterySnapshot ->
                val userSnapshot = lotterySnapshot.documents.filter { it.getString("userId") == userId }
                val existing = userSnapshot.firstOrNull()
                if (existing != null) {
                    val status = existing.getString("status")
                    if (status != EntryStatus.WITHDRAWN.name) {
                        onResult(true, if (status == EntryStatus.WAITING.name) "Already joined." else null)
                        return@addOnSuccessListener
                    }
                }
                fun saveEntry() {
                    if (existing == null) {
                        val newDocRef = entriesRef.document()
                        val entry = LotteryEntry(
                            id = newDocRef.id,
                            lotteryId = lotteryId,
                            userId = userId,
                            status = EntryStatus.WAITING
                        )
                        newDocRef.set(entry.toMap())
                            .addOnSuccessListener { onResult(true, null) }
                    } else {
                        existing.reference.update("status", EntryStatus.WAITING.name)
                            .addOnSuccessListener { onResult(true, null) }
                    }
                }

                if (maxCapacity > 0) {
                    val waitingCount = lotterySnapshot.documents.count { doc ->
                        doc.getString("status") == EntryStatus.WAITING.name
                    }
                    if (waitingCount >= maxCapacity) {
                        onResult(false, "Full.")
                        return@addOnSuccessListener
                    }
                    saveEntry()
                } else {
                    saveEntry()
                }
            }
    }

    /**
     * Allows a user (userId) to leave a waitlist (lotteryId)
     * Checks if user has already joined a lottery in which the status will be changed to WITHDRAWN
     * Collaborators: EntryStatus, Firestore
     */
    fun leaveWaitlist(
        lotteryId: String,
        userId: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        entriesRef.whereEqualTo("lotteryId", lotteryId)
            .get()
            .addOnSuccessListener { snapshot ->
                val snapshotDocs = snapshot.documents.filter { it.getString("userId") == userId }
                if (snapshotDocs.isEmpty()) {
                    onResult(true, null)
                } else {
                    val batch = db.batch()
                    for (doc in snapshotDocs) {
                        batch.update(doc.reference, "status", EntryStatus.WITHDRAWN.name)
                    }
                    batch.commit()
                        .addOnSuccessListener { onResult(true, null) }
                }
            }
    }

    /**
     * Fetches an entrants records for a lottery including if they are WAITING, SELECTED, or WITHDRAWN
     * Collaborators: LotteryEntry, EntryStatus, Firestore
     */
    fun getEntry(
        lotteryId: String,
        userId: String,
        onResult: (Boolean, LotteryEntry?, String?) -> Unit
    ) {
        entriesRef.whereEqualTo("lotteryId", lotteryId)
            .get()
            .addOnSuccessListener { snapshot ->
                try {
                    val doc = snapshot.documents.firstOrNull { it.getString("userId") == userId }
                    if (doc == null) {
                        onResult(true, null, null)
                    } else {
                        val statusStr = doc.getString("status") ?: EntryStatus.WAITING.name
                        val status = try { EntryStatus.valueOf(statusStr) } catch (_: Exception) { EntryStatus.WAITING }
                        val entry = LotteryEntry(
                            id = doc.id,
                            lotteryId = doc.getString("lotteryId") ?: lotteryId,
                            userId = doc.getString("userId") ?: userId,
                            status = status,
                            timestamp = doc.getTimestamp("timestamp")
                        )
                        onResult(true, entry, null)
                    }
                } catch (_: Exception) {
                    onResult(true, null, null)
                }
            }
    }

    /**
     * Fetches all entrant records associated with a specific lottery including if they are WAITING, SELECTED, or WITHDRAWN
     * Collaborators: LotteryEntry, EntryStatus, Firestore
     */
    fun getEntriesLottery(
        lotteryId: String,
        onResult: (Boolean, List<LotteryEntry>, String?) -> Unit
    ) {
        entriesRef.whereEqualTo("lotteryId", lotteryId)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = mutableListOf<LotteryEntry>()
                for (doc in snapshot.documents) {
                    try {
                        val statusStr = doc.getString("status") ?: EntryStatus.WAITING.name
                        val status = try { EntryStatus.valueOf(statusStr) } catch (_: Exception) { EntryStatus.WAITING }
                        list.add(LotteryEntry(
                            id = doc.id,
                            lotteryId = doc.getString("lotteryId") ?: lotteryId,
                            userId = doc.getString("userId") ?: "",
                            status = status,
                            timestamp = doc.getTimestamp("timestamp")
                        ))
                    } catch (_: Exception) {}
                }
                onResult(true, list, null)
            }
    }

    /**
     * Fetches all lottery entries for a specific entrant across all lotteries
     * Collaborators: LotteryEntry, EntryStatus, Firestore
     */
    fun getEntriesEntrant(
        userId: String,
        onResult: (Boolean, List<LotteryEntry>, String?) -> Unit
    ) {
        entriesRef.whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = mutableListOf<LotteryEntry>()
                for (doc in snapshot.documents) {
                    try {
                        val statusStr = doc.getString("status") ?: EntryStatus.WAITING.name
                        val status = try { EntryStatus.valueOf(statusStr) } catch (_: Exception) { EntryStatus.WAITING }
                        list.add(LotteryEntry(
                            id = doc.id,
                            lotteryId = doc.getString("lotteryId") ?: "",
                            userId = doc.getString("userId") ?: userId,
                            status = status,
                            timestamp = doc.getTimestamp("timestamp")
                        ))
                    } catch (_: Exception) {}
                }
                onResult(true, list, null)
            }
    }

    /**
     * Updates the status of a list of lottery entries at the same time
     * Collaborators: LotteryEntry, Firestore
     */
    fun updateStatus(entries: List<LotteryEntry>, onResult: (Boolean, String?) -> Unit) {
        if (entries.isEmpty()) {
            onResult(true, null)
            return
        }
        val batch = db.batch()
        for (entry in entries) {
            val docRef = entriesRef.document(entry.id)
            batch.set(docRef, entry.toMap())
        }
        batch.commit()
            .addOnSuccessListener { onResult(true, null) }
    }
}
