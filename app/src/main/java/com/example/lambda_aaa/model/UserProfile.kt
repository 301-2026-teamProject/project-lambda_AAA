package com.example.lambda_aaa.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

/**
 * Holds information about users
 *
 * Each instance corresponds to a unique user stored in the database which holds all the relevant
 * information about the user needed by other methods or functions.
 *
 * @property uid the unique ID corresponding to the user determined by Firestore
 * @property deviceID the second unique ID corresponding to the specific installation on the user's device
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
    private var deviceID: String = "",
    private var name: String = "",
    private var email: String = "",
    private var phone: String = "",
    private var profilePhotoUrl: String = "",
    private var roles: ArrayList<Role> = arrayListOf(),
    private var notificationsEnabled: Boolean = true,
    private var isOwner: Boolean = false,
    private val creationDate: String = ""
) {

    /**
     * Gets the user's name
     * @return the user's name
     */
    val userName : String
        get() = name

    /**
     * Gets the user's email
     * @return the user's email
     */
    val userEmail : String
        get() = email

    /**
     * Gets the user's phone number
     * @return the user's phone number
     */
    val userPhone : String
        get() = phone

    /**
     * Gets the user's profile picture URL
     * @return the user's profile picture URL
     */
    val userPhoto : String
        get() = profilePhotoUrl

    /**
     * Gets the user's roles
     * @return the user's roles
     */
    val userRole : Role
        get() = roles[0]

    /**
     * Gets the user's device ID
     * @return the user's device ID
     */
    val userDeviceID : String
        get() = deviceID

    /**
     * Toggle on and off the user's ability to receive notifications
     */
    fun toggleNotifications() : Unit {
        notificationsEnabled = !notificationsEnabled
    }

    /**
     * Update the user's name
     * @param newName the new name that will replace the old name
     */
    fun updateName(newName: String) : Unit {
        name = newName
    }

    /**
     * Update the user's email
     * @param newEmail the new email that will replace the old email
     */
    fun updateEmail(newEmail: String) : Unit {
        email = newEmail
    }

    /**
     * Update the user's phone number
     * @param newPhone the new phone number that will replace the old phone number
     */
    fun updatePhone(newPhone: String) : Unit {
        phone = newPhone
    }

    /**
     * Update the user's profile picture URL
     * @param newPhoto the new URL that will replace the old URL
     */
    fun updatePhoto(newPhoto: String) : Unit {
        profilePhotoUrl = newPhoto
    }

    /**
     * Update the user's device ID
     * @param newDeviceID the new device ID that will replace the old device ID
     */
    fun updateDeviceID(newDeviceID: String) : Unit {
        deviceID = newDeviceID
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "deviceID" to userDeviceID,
        "name" to userName,
        "email" to userEmail,
        "phone" to userPhone,
        "profilePhotoUrl" to userPhoto,
        "roles" to userRole,
    )

}