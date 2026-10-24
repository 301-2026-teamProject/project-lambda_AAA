package com.example.lambda_aaa.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.CampsiteRepository

class CampsiteDetailController(
    private val cid: String,
    private val repository: CampsiteRepository
) {
    var campsite by mutableStateOf<Campsite?>(null)
    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchCampsiteAndLottery()
    }

    fun fetchCampsiteAndLottery() {
        /* also get lottery of this campsite */
        repository.getCampsite(cid = cid) { success, campsite, msg ->
            if (success) {
                this.campsite = campsite
            } else {
                errorMessage = "Failed to load campsites: $msg"
            }
            isLoading = false
        }
    }
}