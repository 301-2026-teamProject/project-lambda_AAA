package com.example.lambda_aaa.controller

import com.example.lambda_aaa.model.UserProfilesRepository

class ProfileController {

    private val profileRepo = UserProfilesRepository()


    fun updateBasicInfo(name: String, email: String, phone: String, deviceId: String) {
        profileRepo.updateProfile(name, email, phone, deviceId)
    }

    fun updateProfilePhoto(photo: String, deviceId: String) {
        profileRepo.updateImage(photo, deviceId)
    }

}