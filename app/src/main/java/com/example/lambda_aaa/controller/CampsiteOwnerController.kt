package com.example.lambda_aaa.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.CampsiteRepository

/**
 * Designated controller for loading information for the campsite owner views.
 *
 * Load information from CampsiteRepository into CampsiteOwnerView
 *
 * @property uid user id of the campsite owner to retrieve campsites of that owner
 * @property repository stores reference to the CampsiteRepository
 * @property campsites stores campsite data
 * @property isLoading stores campsite loading status
 * @property errorMessage stores error message if error occurs; null otherwise
 */
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
        isLoading = true
        errorMessage = null
        repository.getOwnerCampsites(ownerId = uid) { success, campsites, msg ->
            if (success) {
                this.campsites = campsites
            } else {
                errorMessage = "Failed to load campsites: $msg"
            }
            isLoading = false
        }
    }

    /**
     * campsite repository class proxy
     *
     * @param cid provide cid for repo class to search the Firestore database
     * @return the campsite if search match; null otherwise
     */
    fun fetchCampsite(cid: String): Campsite? = campsites.find { it.cid == cid }
}