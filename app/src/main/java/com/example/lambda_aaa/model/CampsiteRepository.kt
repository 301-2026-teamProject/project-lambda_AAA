package com.example.lambda_aaa.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.firestore.ListenerRegistration
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlin.jvm.java

class CampsiteRepository {
    private val _campsites = mutableStateListOf<Campsite>()
    private val campsiteDb = Firebase.firestore
    private val campsitesRef = campsiteDb.collection("campsites")

    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    private var listener: ListenerRegistration? = null

    init { refresh() }

    fun refresh() {
        listener?.remove()
        isLoading = true
        errorMessage = null
        listener = campsitesRef.addSnapshotListener { snapshots, exception ->
            isLoading = false
            _campsites.clear()
            if (exception != null) {
                errorMessage = "Could not load campsites. Check your connection and try again."
            } else {
                try {
                    _campsites.addAll(snapshots?.toObjects(Campsite::class.java).orEmpty())
                    errorMessage = null
                } catch (_: RuntimeException) {
                    errorMessage = "Campsite information could not be read. Please try again later."
                }
            }
        }
    }

    fun close() { listener?.remove() }

    fun newCampsiteId(): String = campsitesRef.document().id
    val campsiteList: List<Campsite> = _campsites

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

    // get campsite details and also used in campsite information editing
    fun getCampsite(cid: String, onResult: (Boolean, Campsite?, String?) -> Unit) {
        campsitesRef.document(cid).get()
            .addOnSuccessListener { snapshot ->
                val campsite = try {
                    snapshot.toObject(Campsite::class.java)
                } catch (e: RuntimeException) {
                    onResult(false, null, e.message)
                    return@addOnSuccessListener
                }
                onResult(true, campsite, null)
            }
            .addOnFailureListener { e ->
                onResult(false, null, e.message)
            }
    }

    fun getOwnerCampsites(ownerId: String, onResult: (Boolean, List<Campsite>, String?) -> Unit) {
        campsitesRef
            .whereEqualTo("ownerId", ownerId)
            .get()
            .addOnSuccessListener { snapshot ->
                onResult(true, snapshot.toObjects(Campsite::class.java), null)
            }
            .addOnFailureListener { e ->
                onResult(false, emptyList(), e.message)
            }
    }

    fun getAllCampsites(onResult: (Boolean, List<Campsite>, String?) -> Unit) {
        campsitesRef.get()
            .addOnSuccessListener { snapshot ->
                onResult(true, snapshot.toObjects(Campsite::class.java), null)
            }
            .addOnFailureListener { e ->
                onResult(false, emptyList(), e.message)
            }
    }
}