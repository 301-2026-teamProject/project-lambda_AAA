package com.example.lambda_aaa.model

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.FieldPath

class UserProfilesRepository {

    private val userProfiles = mutableStateListOf<UserProfile>()
    private val db = Firebase.firestore
    private val usersCollection = db.collection("users")

    init { getprofiles() }

    fun getprofiles() {

        usersCollection.get()
            .addOnSuccessListener { profiles ->

                if (!profiles.isEmpty) {
                    userProfiles.addAll(profiles.toObjects(UserProfile::class.java))
                }
            }
            .addOnFailureListener { e ->
                Log.e("Profile Collection Error", "Unable to get Profiles", e)
            }

    }

    fun updateProfile(
        newName: String,
        newEmail: String,
        newPhone: String,
        deviceId: String
    ) {

        val profileMatch: UserProfile? = userProfiles.find {it.deviceId == deviceId}
        val profileIndex = userProfiles.indexOf(profileMatch)
        profileMatch.apply {
            this?.name = newName
            this?.email = newEmail
            this?.phone = newPhone
        }
        if (profileMatch != null) {
            userProfiles[profileIndex] = profileMatch
            usersCollection.document(profileMatch.uid).set(profileMatch)
        }

    }

    fun updateImage(imageURL: String, deviceId: String) {

        val profileMatch: UserProfile? = userProfiles.find {it.deviceId == deviceId}
        val profileIndex = userProfiles.indexOf(profileMatch)
        profileMatch.apply {
            this?.profilePhotoUrl = imageURL
        }
        if (profileMatch != null) {
            userProfiles[profileIndex] = profileMatch
            usersCollection.document(profileMatch.uid).set(profileMatch)
        }
    }

    /**
     * Looks up display names for a set of user ids.
     * Never fails: missing or blank names are simply absent from the returned map.
     */
    fun getNames(uids: Set<String>, onResult: (Map<String, String>) -> Unit) {
        if (uids.isEmpty()) {
            onResult(emptyMap())
            return
        }
        // whereIn on document id accepts at most 10 values, so query in chunks
        val chunks = uids.chunked(10)
        val names = mutableMapOf<String, String>()
        var remaining = chunks.size
        chunks.forEach { chunk ->
            usersCollection.whereIn(FieldPath.documentId(), chunk).get()
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