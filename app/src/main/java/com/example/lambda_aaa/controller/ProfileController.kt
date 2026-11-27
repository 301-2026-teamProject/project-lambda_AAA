package com.example.lambda_aaa.controller

import android.content.Context
import android.util.Log
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.CampsiteRepository
import com.example.lambda_aaa.model.EntryRepository
import com.example.lambda_aaa.model.LotteryEntry
import com.example.lambda_aaa.model.Role
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

    /** Removes a Profile from the database, and any linked data (i.e. Campsites if Owner, or Lottery Entries if Entrant)
     *
     * @param context the localContext that CampsiteManagementController uses
     */
    fun deleteProfile(
        context: Context
    ) {
        if (user.roles.contains(Role.OWNER)) { // If the user is an owner...
            val campsiteRepo = CampsiteRepository()
            var campsiteList: List<Campsite> = emptyList() // Then we have to get rid of all the campsites...
            campsiteRepo.getAllCampsites { check, campsites, _ ->
                if (check) {
                    campsiteList = campsites
                }
            }
            for (campsite in campsiteList) { //... and delete the Owners campsites (since we're deleting their profile)
                if (campsite.ownerId == user.uid) {
                    val campsiteController = CampsiteManagementController(cid = campsite.cid, uid = campsite.ownerId, isNew = false, repository = campsiteRepo, context = context)
                    campsiteController.deleteCampsite(campsite) { onFail -> Log.e("Campsite Deletion Error", onFail)}
                }
            }
        }

        if (user.roles.contains(Role.ENTRANT)) { // If the user is an entrant...
            val entryRepo = EntryRepository()
            var entries: List<LotteryEntry> = emptyList() // ...we have to remove all lottery entries
            entryRepo.getEntriesEntrant(user.uid) { check, listEntry, _ ->
                if (check) {
                    entries = listEntry // Gets all Lottery Entries that match the currently indexed lottery ID
                }
            }
            for (entry in entries) {
                entryRepo.deleteEntry(entry) { check, _ -> if (!check) Log.e("Lottery Entry Deletion Error", "") } // Removes all entries
            }
        }

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