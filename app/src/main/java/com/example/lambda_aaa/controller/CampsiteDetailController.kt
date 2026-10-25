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

    fun fetchCampsiteAndLottery() {
        //fetch this campsites lottery and booking dates when the shared lottery model is finished
        isLoading = true
        errorMessage = null
        campsite = null
        repository.getCampsite(cid = cid) { success, campsite, _ ->
            if (success && campsite != null) {
                this.campsite = campsite
            } else {
                errorMessage = if (success) "Campsite does not exist."
                    else "Could not load campsite. Check your connection and try again."
            }
            isLoading = false
        }
    }
}
