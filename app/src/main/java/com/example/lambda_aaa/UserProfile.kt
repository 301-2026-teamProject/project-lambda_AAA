package com.example.lambda_aaa

class UserProfile(
    var name: String,
    var email: String,
    var phoneNumber: String,
    var role: String
) {

    private var receiveNotifications: Boolean = true

    fun toggleNotifications() : Unit {
        receiveNotifications = !receiveNotifications
    }

    fun checkRole(): String {
        return role
    }

    fun updateProfile() : Unit {
        // do something
    }
}