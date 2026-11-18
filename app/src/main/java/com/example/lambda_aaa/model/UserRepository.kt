package com.example.lambda_aaa.model

import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.firestore

/**
 * Read-only access to the "users" collection (document id == uid).
 */
class UserRepository {
    private val usersRef = Firebase.firestore.collection("users")

    /**
     * Looks up display names for a set of user ids.
     *
     * Never fails: uids that are missing, unreadable, or have a blank name are simply absent from
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
