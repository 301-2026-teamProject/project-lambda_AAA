package com.example.lambda_aaa.model

import com.google.firebase.firestore.DocumentId

data class UserProfile(
    @DocumentId val uid: String,
    private var name: String,
    private var email: String,
    private var phone: String,
    private var profilePhotoUrl: String,
    private var roles: ArrayList<Role>,
    private var notificationsEnabled: Boolean = true,
    private var isOwner: Boolean = false,
    private val creationDate: String
)