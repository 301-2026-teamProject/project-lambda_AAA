package com.example.lambda_aaa.model

import com.google.firebase.firestore.DocumentId

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

    // Custom getters for private attributes
    val userName : String
        get() = name
    val userEmail : String
        get() = email
    val userPhone : String
        get() = phone
    val userPhoto : String
        get() = profilePhotoUrl
    val userRole : Role
        get() = roles[0]
    val userDeviceID : String
        get() = deviceID

    fun toggleNotifications() : Unit {
        notificationsEnabled = !notificationsEnabled
    }

    // Functions to update UserProfile attributes used in profile view
    fun updateName(newName: String) : Unit {
        name = newName
    }
    fun updateEmail(newEmail: String) : Unit {
        email = newEmail
    }
    fun updatePhone(newPhone: String) : Unit {
        phone = newPhone
    }
    fun updatePhoto(newPhoto: String) : Unit {
        profilePhotoUrl = newPhoto
    }
}