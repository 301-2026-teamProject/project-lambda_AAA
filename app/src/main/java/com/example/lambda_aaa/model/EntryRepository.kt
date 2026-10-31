package com.example.lambda_aaa.model
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class EntryRepository {
    private val db = Firebase.firestore
    private val entriesRef = db.collection("lottery_entries")
    fun joinWaitlist(
        lotteryId: String,
        userId: String,
        maxCapacity: Int,
        onResult: (Boolean, String?) -> Unit
    ) {
        entriesRef.whereEqualTo("lotteryId", lotteryId)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { userSnapshot ->
                if (!userSnapshot.isEmpty) {
                    val doc = userSnapshot.documents[0]
                    val currentStatus = doc.getString("status")
                    if (currentStatus == EntryStatus.WAITING.name) {
                        onResult(false, "Already joined.")
                        return@addOnSuccessListener
                    } else {
                        doc.reference.update("status", EntryStatus.WAITING.name)
                            .addOnSuccessListener { onResult(true, null) }
                            .addOnFailureListener { e -> onResult(false, e.message) }
                        return@addOnSuccessListener
                    }
                }
                if (maxCapacity > 0) {
                    entriesRef.whereEqualTo("lotteryId", lotteryId)
                        .get()
                        .addOnSuccessListener { lotterySnapshot ->
                            val waitingCount = lotterySnapshot.documents.count { doc ->
                                doc.getString("status") == EntryStatus.WAITING.name
                            }
                            if (waitingCount >= maxCapacity) {
                                onResult(false, "Full.")
                                return@addOnSuccessListener
                            }
                            createNewEntry(lotteryId, userId, onResult)
                        }
                        .addOnFailureListener { e -> onResult(false, e.message) }
                } else {
                    createNewEntry(lotteryId, userId, onResult)
                }
            }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
    private fun createNewEntry(lotteryId: String, userId: String, onResult: (Boolean, String?) -> Unit) {
        val newDocRef = entriesRef.document()
        val entry = LotteryEntry(
            id = newDocRef.id,
            lotteryId = lotteryId,
            userId = userId,
            status = EntryStatus.WAITING
        )
        newDocRef.set(entry.toMap())
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
    fun leaveWaitlist(
        lotteryId: String,
        userId: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        entriesRef.whereEqualTo("lotteryId", lotteryId)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    onResult(false, "No waitlist found.")
                } else {
                    val batch = db.batch()
                    for (doc in snapshot.documents) {
                        batch.update(doc.reference, "status", EntryStatus.WITHDRAWN.name)
                    }
                    batch.commit()
                        .addOnSuccessListener { onResult(true, null) }
                        .addOnFailureListener { e -> onResult(false, e.message) }
                }
            }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
    fun getEntry(
        lotteryId: String,
        userId: String,
        onResult: (Boolean, LotteryEntry?, String?) -> Unit
    ) {
        entriesRef.whereEqualTo("lotteryId", lotteryId)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    onResult(true, null, null)
                } else {
                    val entry = snapshot.documents[0].toObject(LotteryEntry::class.java)
                    onResult(true, entry, null)
                }
            }
            .addOnFailureListener { e ->
                onResult(false, null, e.message)
            }
    }
    fun getEntriesLottery(
        lotteryId: String,
        onResult: (Boolean, List<LotteryEntry>, String?) -> Unit
    ) {
        entriesRef.whereEqualTo("lotteryId", lotteryId)
            .get()
            .addOnSuccessListener { snapshot ->
                val entries = snapshot.toObjects(LotteryEntry::class.java)
                onResult(true, entries, null)
            }
            .addOnFailureListener { e ->
                onResult(false, emptyList(), e.message)
            }
    }
    fun getEntriesEntrant(
        userId: String,
        onResult: (Boolean, List<LotteryEntry>, String?) -> Unit
    ) {
        entriesRef.whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val entries = snapshot.toObjects(LotteryEntry::class.java)
                onResult(true, entries, null)
            }
            .addOnFailureListener { e ->
                onResult(false, emptyList(), e.message)
            }
    }
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
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
}
