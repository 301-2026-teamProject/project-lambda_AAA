package com.example.lambda_aaa.model

import com.google.firebase.firestore.DocumentId

data class UserProfile(
    @DocumentId val uid: String,
    var name: String,
    var email: String,
    var phone: String,
    var profilePhotoUrl: String,
    var roles: ArrayList<Role>,
    var notificationsEnabled: Boolean = true,
    var isOwner: Boolean = false,
    val creationDate: String
)