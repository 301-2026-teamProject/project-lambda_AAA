package com.example.lambda_aaa.controller

import android.util.Log
import com.example.lambda_aaa.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ProfileController (private val user: UserProfile) {

    private val db = Firebase.firestore
    private val usersCollection = db.collection("users")

    fun getUserName(): String {
        return user.name
    }

    fun getUserEmail() : String {
        return user.email
    }

    fun getUserPhone() : String {
        return user.phone
    }

    fun getUserPhoto() : String {
        return user.profilePhotoUrl
    }

    fun toggleUserNotifications() {
        user.toggleNotifications()
    }

    fun updateBasicInfo(newName: String, newEmail: String, newPhone: String) {

        user.updateName(newName)
        user.updateEmail(newEmail)
        user.updatePhone(newPhone)

        val newInfo = mapOf(
            "name" to newName,
            "email" to newEmail,
            "phone" to newPhone
        )

        usersCollection.whereEqualTo("deviceId", user.deviceId).limit(1)
            .get()
            .addOnSuccessListener { profileRef ->

                if (!profileRef.isEmpty) {
                    profileRef.documents[0].reference.update(newInfo)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Profile Basic Info Update Error", "Failed to update basic info", e)
            }

    }

    fun updateProfilePhoto(newPhoto: String) {

        user.updatePhoto(newPhoto)

        val newInfo = mapOf(
            "profilePhotoUrl" to newPhoto
        )

        usersCollection.whereEqualTo("deviceId", user.deviceId).limit(1)
            .get()
            .addOnSuccessListener { profileRef ->

                if (!profileRef.isEmpty) {
                    profileRef.documents[0].reference.update(newInfo)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Profile Photo Update Error", "Failed to update photo", e)
            }

    }

}