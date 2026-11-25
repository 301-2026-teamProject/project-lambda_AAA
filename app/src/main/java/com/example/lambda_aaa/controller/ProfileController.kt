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

        val newInfo = mapOf(
            "notificationsEnabled" to user.notificationsEnabled,
        )

        usersCollection.whereEqualTo("deviceId", user.deviceId).limit(1)
            .get()
            .addOnSuccessListener { profileRef ->

                if (!profileRef.isEmpty) {
                    profileRef.documents[0].reference.update(newInfo)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Profile Toggle Notifications Error", "Failed to update notifications", e)
            }
    }

    fun updateProfile(
        newName: String = user.name,
        newEmail: String = user.email,
        newPhone: String = user.phone,
        newPhoto: String = user.profilePhotoUrl
    ) {

        user.updateName(newName)
        user.updateEmail(newEmail)
        user.updatePhone(newPhone)
        user.updatePhoto(newPhoto)

        val newInfo = mapOf(
            "name" to newName,
            "email" to newEmail,
            "phone" to newPhone,
            "profilePhotoUrl" to newPhoto
        )

        usersCollection.whereEqualTo("deviceId", user.deviceId).limit(1)
            .get()
            .addOnSuccessListener { match ->

                if (!match.isEmpty) {
                    match.documents[0].reference.update(newInfo)
                }
                else {
                    usersCollection.add(user)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Profile Update Error", "Failed to update profile", e)
            }

    }

    fun deleteProfile() {

        usersCollection.whereEqualTo("deviceId", user.deviceId).limit(1)
            .get()
            .addOnSuccessListener { match ->

                if (!match.isEmpty) {
                    match.documents[0].reference.delete()
                }

            }
            .addOnFailureListener { e ->
                Log.e("Profile Deletion Error", "Failed to delete profile", e)
            }
    }

}