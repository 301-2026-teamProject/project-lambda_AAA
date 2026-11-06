package com.example.lambda_aaa.model

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.compose.runtime.mutableStateListOf
import android.util.Log

//LotteryRepository stores all lotteries
// On start obtains all lotteries from FireStore
// Provides methods to update whole lottery / lotteries or individual fields
// Provides getter methods for lottery specified through a lottery ID
class LotteryRepository {

    private val _lotteries = mutableStateListOf<Lottery>()
    private val campsiteDb = Firebase.firestore
    private val lotteryRef = campsiteDb.collection("lotteries")

    private val listener = lotteryRef.addSnapshotListener { snapshots, exception ->
            if (exception != null) {
                Log.e("LotteryRepo", "Listen failed", exception)
                return@addSnapshotListener
            }

            val decoded = snapshots?.documents.orEmpty().mapNotNull { doc ->
                runCatching { Lottery.fromMap(doc.id, doc.data.orEmpty()) }
                    .onFailure { Log.w("LotteryRepo", "Skipping bad lottery ${doc.id}", it) }
                    .getOrNull()
            }

            _lotteries.clear()
            _lotteries.addAll(decoded)
        }

    fun close() { listener.remove() }

    fun create(lottery: Lottery, onResult: (Boolean, String?) -> Unit) {
        lotteryRef.document(lottery.id).set(lottery.toMap())
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun updateLottery(lottery: Lottery, onResult: (Boolean, String?) -> Unit) {
        val docRef = lotteryRef.document(lottery.id)
        docRef.get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    onResult(false, "Lottery does not exist.")
                    return@addOnSuccessListener
                }
                docRef.set(lottery.toMap())
                    .addOnSuccessListener { onResult(true, null) }
                    .addOnFailureListener { e -> onResult(false, e.message) }
            }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun getLottery(id: String, onResult: (Boolean, Lottery?, String?) -> Unit) {
        lotteryRef.document(id).get()
            .addOnSuccessListener { snapshot ->
                val lottery = try {
                    snapshot.data?.let { Lottery.fromMap(snapshot.id, it) }
                } catch (e: RuntimeException) {
                    onResult(false, null, e.message)
                    return@addOnSuccessListener
                }
                onResult(true, lottery, null)
            }
            .addOnFailureListener { e ->
                onResult(false, null, e.message)
            }
    }

    fun getAllLotteries(onResult: (Boolean, List<Lottery>, String?) -> Unit) {
        lotteryRef.get()
            .addOnSuccessListener { snapshot ->
                val lotteries = try {
                    snapshot.documents.map { Lottery.fromMap(it.id, it.data.orEmpty()) }
                } catch (e: RuntimeException) {
                    onResult(false, emptyList(), e.message)
                    return@addOnSuccessListener
                }
                onResult(true, lotteries, null)
            }
            .addOnFailureListener { e ->
                onResult(false, emptyList(), e.message)
            }
    }

    fun updateNumWinners(lottery: Lottery, num: Int) {
        lottery.numWinners = num
        lotteryRef.document(lottery.id).update("numWinners", num)
            .addOnFailureListener { Log.e("LotteryRepo", "Update failed", it) }
    }

    fun updateBookingPeriod(lottery: Lottery, period: DateRange) {
        lottery.bookingPeriod = period
        lotteryRef.document(lottery.id).update(
            mapOf(
                "bookingPeriodStart" to period.start.toString(),
                "bookingPeriodEnd" to period.end.toString()
            )
        ).addOnFailureListener { Log.e("LotteryRepo", "Update failed", it) }
    }

    fun updateRegistrationPeriod(lottery: Lottery, period: DateRange) {
        lottery.registrationPeriod = period
        lotteryRef.document(lottery.id).update(
            mapOf(
                "registrationPeriodStart" to period.start.toString(),
                "registrationPeriodEnd" to period.end.toString()
            )
        ).addOnFailureListener { Log.e("LotteryRepo", "Update failed", it) }
    }

    fun updateWaitlistCapacity(lottery: Lottery, num: Int) {
        lottery.waitlistCapacity = num
        lotteryRef.document(lottery.id).update("waitlistCapacity", num)
            .addOnFailureListener { Log.e("LotteryRepo", "Update failed", it) }
    }
}
