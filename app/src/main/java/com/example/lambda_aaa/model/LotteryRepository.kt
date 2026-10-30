package com.example.lambda_aaa.model

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import android.util.Log

class LotteryRepository(db: FirebaseFirestore = Firebase.firestore) {
    private val lotteries = db.collection("lotteries")

    fun create(lottery: Lottery) {
        lotteries.document(lottery.id).set(lottery.toMap())
            .addOnFailureListener { Log.e("LotteryRepo", "Create failed", it) }
    }

    fun updateNumWinners(lottery: Lottery, num: Int) {
        lottery.numWinners = num
        lotteries.document(lottery.id).update("numWinners", num)
            .addOnFailureListener { Log.e("LotteryRepo", "Update failed", it) }
    }

    fun updateBookingPeriod(lottery: Lottery, period: DateRange) {
        lottery.bookingPeriod = period
        lotteries.document(lottery.id).update(
            mapOf(
                "bookingPeriodStart" to period.start.toString(),
                "bookingPeriodEnd" to period.end.toString()
            )
        ).addOnFailureListener { Log.e("LotteryRepo", "Update failed", it) }
    }

    fun updateRegistrationPeriod(lottery: Lottery, period: DateRange) {
        lottery.bookingPeriod = period
        lotteries.document(lottery.id).update(
            mapOf(
                "registrationPeriodStart" to period.start.toString(),
                "registrationPeriodEnd" to period.end.toString()
            )
        ).addOnFailureListener { Log.e("LotteryRepo", "Update failed", it) }
    }
}