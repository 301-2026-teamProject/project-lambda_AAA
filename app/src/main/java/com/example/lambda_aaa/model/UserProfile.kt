package com.example.lambda_aaa.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Holds information about users
 *
 * Each instance corresponds to a unique user stored in the database which holds all the relevant
 * information about the user needed by other methods or functions.
 *
 * @property uid the unique ID corresponding to the user determined by Firestore
 * @property deviceId the second unique ID corresponding to the specific installation on the user's device
 * @property name the name of the user
 * @property email the email of the user
 * @property phone the phone number of the user
 * @property profilePhotoUrl the URL link to the user's profile picture
 * @property roles the roles that the user has that is recognized by the app
 * @property notificationsEnabled the identifier that determines if the user receives notifications
 * @property isOwner the identifier if the user is registered as the owner
 * @property creationDate the date when the user is first created
 */
data class UserProfile(
    @DocumentId val uid: String = "",
    var deviceId: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var profilePhotoUrl: String = "",
    var roles: ArrayList<Role> = arrayListOf(),
    var notificationsEnabled: Boolean = true,
    var isOwner: Boolean = false,
    @ServerTimestamp val creationDate: Timestamp? = null
) {

    /**
     * Toggle on and off the user's ability to receive notifications
     */
    fun toggleNotifications() {
        notificationsEnabled = !notificationsEnabled
    }

    /**
     * Update the user's name
     * @param newName the new name that will replace the old name
     */
    fun updateName(newName: String) {
        name = newName
    }

    /**
     * Update the user's email
     * @param newEmail the new email that will replace the old email
     */
    fun updateEmail(newEmail: String) {
        email = newEmail
    }

    /**
     * Update the user's phone number
     * @param newPhone the new phone number that will replace the old phone number
     */
    fun updatePhone(newPhone: String) {
        phone = newPhone
    }

    /**
     * Update the user's profile picture URL
     * @param newPhoto the new URL that will replace the old URL
     */
    fun updatePhoto(newPhoto: String) {
        profilePhotoUrl = newPhoto
    }

    /**
     * Update the user's device ID
     * @param newDeviceId the new device ID that will replace the old device ID
     */
    fun updateDeviceId(newDeviceId: String) {
        deviceId = newDeviceId
    }


}