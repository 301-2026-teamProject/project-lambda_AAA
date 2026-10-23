package com.example.lambda_aaa.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.CampsiteRepository

class CampsiteDetailController(
    private val uid: String,
    private val ownerSearch: Boolean,
    private val repository: CampsiteRepository
) {
    var campsites by mutableStateOf(listOf<Campsite>())
    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        if (ownerSearch) {
            repository.getOwnerCampsites(ownerId = uid) { success, campsites ->
                if (success) {
                    this.campsites = campsites
                } else {
                    errorMessage = "Failed to load campsites."
                }
                isLoading = false
            }
        } else {
            repository.getAllCampsites { success, campsites ->
                if (success) {
                    this.campsites = campsites
                } else {
                    errorMessage = "Failed to load campsites."
                }
                isLoading = false
            }
        }
    }
}