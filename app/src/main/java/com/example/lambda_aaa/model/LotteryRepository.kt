package com.example.lambda_aaa.model

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.compose.runtime.mutableStateListOf

class LotteryRepository {

    private val _lotteries = mutableStateListOf<Lottery>()
    private val campsiteDb = Firebase.firestore
    private val lotteryRef = campsiteDb.collection("lotteries")

    init {
        lotteryRef.addSnapshotListener { snapshots, exception ->
            if (exception != null) {
                return@addSnapshotListener
            }

            _lotteries.clear()

            snapshots?.toObjects(Lottery::class.java)?.let {
                _lotteries.clear()
                _lotteries.addAll(it)
            }
        }
    }

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
                docRef.set(lottery)
                    .addOnSuccessListener { onResult(true, null) }
                    .addOnFailureListener { e -> onResult(false, e.message) }
            }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun getLottery(id: String, onResult: (Boolean, Lottery?, String?) -> Unit) {
        lotteryRef.document(id).get()
            .addOnSuccessListener { snapshot ->
                onResult(true, snapshot.toObject(Lottery::class.java), null)
            }
            .addOnFailureListener { e ->
                onResult(false, null, e.message)
            }
    }

    fun getAllLotteries(onResult: (Boolean, List<Lottery>, String?) -> Unit) {
        lotteryRef.get()
            .addOnSuccessListener { snapshot ->
                onResult(true, snapshot.toObjects(Lottery::class.java), null)
            }
            .addOnFailureListener { e ->
                onResult(false, emptyList(), e.message)
            }
    }
}