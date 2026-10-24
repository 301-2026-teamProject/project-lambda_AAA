package com.example.lambda_aaa.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.CampsiteRepository

class CampsiteOwnerController(
    private val uid: String,
    private val repository: CampsiteRepository
) {
    var campsites by mutableStateOf(listOf<Campsite>())
    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        refresh()
    }

    fun refresh() {
        repository.getOwnerCampsites(ownerId = uid) { success, campsites, msg ->
            if (success) {
                this.campsites = campsites
            } else {
                errorMessage = "Failed to load campsites: $msg"
            }
            isLoading = false
        }
    }

    fun fetchCampsite(cid: String): Campsite? = campsites.find { it.cid == cid }
}