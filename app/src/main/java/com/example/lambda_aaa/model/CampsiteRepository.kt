package com.example.lambda_aaa.model

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlin.jvm.java

class CampsiteRepository {
    private val _campsites = mutableStateListOf<Campsite>()
    private val campsiteDb = Firebase.firestore
    private val campsitesRef = campsiteDb.collection("campsites")

    init {
        campsitesRef.addSnapshotListener { snapshots, exception ->
            if (exception != null) {
                return@addSnapshotListener
            }

            _campsites.clear()

            snapshots?.toObjects(Campsite::class.java)?.let {
                _campsites.clear()
                _campsites.addAll(it)
            }
        }
    }

    fun addCampsite(campsite: Campsite, onResult: (Boolean, String?) -> Unit) {
        campsite.creationDate = null
        campsite.lastUpdatedDate = null
        campsitesRef.document(campsite.cid).set(campsite)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun deleteCampsite(campsite: Campsite, onResult: (Boolean, String?) -> Unit) {
        campsitesRef.document(campsite.cid).delete()
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message)  }
    }

    fun updateCampsite(campsite: Campsite, onResult: (Boolean, String?) -> Unit) {
        val docRef = campsitesRef.document(campsite.cid)
        docRef.get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    onResult(false, "Campsite does not exist.")
                    return@addOnSuccessListener
                }
                campsite.lastUpdatedDate = null
                docRef.set(campsite)
                    .addOnSuccessListener { onResult(true, null) }
                    .addOnFailureListener { e -> onResult(false, e.message) }
            }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    /* get campsite details and also used in campsite information editing */
    fun getCampsite(cid: String, onResult: (Boolean, Campsite?) -> Unit) {
        campsitesRef.document(cid).get()
            .addOnSuccessListener { snapshot ->
                onResult(true, snapshot.toObject(Campsite::class.java))
            }
            .addOnFailureListener {
                onResult(false, null)
            }
    }

    fun newCampsiteId(): String = campsitesRef.document().id

    fun getOwnerCampsites(ownerId: String, onResult: (Boolean, List<Campsite>) -> Unit) {
        campsitesRef
            .whereEqualTo("ownerId", ownerId)
            .get()
            .addOnSuccessListener { snapshot ->
                onResult(true, snapshot.toObjects(Campsite::class.java))
            }
            .addOnFailureListener {
                onResult(false, emptyList())
            }
    }

    fun getAllCampsites(onResult: (Boolean, List<Campsite>) -> Unit) {
        campsitesRef.get()
            .addOnSuccessListener { snapshot ->
                onResult(true, snapshot.toObjects(Campsite::class.java))
            }
            .addOnFailureListener {
                onResult(false, emptyList())
            }
    }
}