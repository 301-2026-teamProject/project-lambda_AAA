package com.example.lambda_aaa.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlin.collections.orEmpty

/**
 * Read-only access to the "users" collection (document id == uid).
 */
class UserRepository {
    private val _profiles = mutableStateListOf<UserProfile>()
    private val usersRef = Firebase.firestore.collection("users")
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
        listener = usersRef.addSnapshotListener { snapshots, exception ->
            isLoading = false
            _profiles.clear()
            if (exception != null) {
                errorMessage = "Could not load profiles. Check your connection and try again."
            } else {
                try {
                    _profiles.addAll(snapshots?.documents?.mapNotNull { decode(it) }.orEmpty())
                    errorMessage = null
                } catch (_: RuntimeException) {
                    errorMessage = "Profile information could not be read. Please try again later."
                }
            }
        }
    }

    fun close() { listener?.remove() }
    fun getAllProfiles(onResult: (Boolean, List<UserProfile>, String?) -> Unit) {
        usersRef.get()
            .addOnSuccessListener { snapshot ->
                deliverList(snapshot, onResult)
            }
            .addOnFailureListener { e ->
                onResult(false, emptyList(), e.message)
            }
    }

    private fun decode(snapshot: DocumentSnapshot): UserProfile? =
        snapshot.data?.let { ProfileFirestoreMapper.decode( snapshot.id, it) }

    private fun deliverList(snapshot: QuerySnapshot, onResult: (Boolean, List<UserProfile>, String?) -> Unit) {
        val profiles = try {
            snapshot.documents.mapNotNull { decode(it) }
        } catch (e: RuntimeException) {
            onResult(false, emptyList(), e.message)
            return
        }
        onResult(true, profiles, null)
    }

    /**
     * Looks up display names for a set of user ids.
     *
     * Never fails: UID's that are missing, unreadable, or have a blank name are simply absent from
     * the returned map, so callers should fall back to a default label.
     *
     * @param uids user ids to look up
     * @param onResult called once with a map of uid -> name
     */
    fun getNames(uids: Set<String>, onResult: (Map<String, String>) -> Unit) {
        if (uids.isEmpty()) {
            onResult(emptyMap())
            return
        }
        // whereIn on document id accepts a limited number of values, so query in small chunks
        val chunks = uids.chunked(10)
        val names = mutableMapOf<String, String>()
        var remaining = chunks.size
        chunks.forEach { chunk ->
            usersRef.whereIn(FieldPath.documentId(), chunk).get()
                .addOnSuccessListener { snapshot ->
                    snapshot.documents.forEach { doc ->
                        doc.getString("name")?.takeIf { it.isNotBlank() }?.let { names[doc.id] = it }
                    }
                }
                .addOnCompleteListener {
                    remaining--
                    if (remaining == 0) onResult(names)
                }
        }
    }
}