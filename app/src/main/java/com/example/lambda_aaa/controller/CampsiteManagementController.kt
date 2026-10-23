package com.example.lambda_aaa.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.CampsiteRepository

/**
 * Controller: loads the campsite by cid, and pushes the edited values
 * back through CampsiteRepository. The view never touches the repository.
 */
class CampsiteManagementController(
    private val cid: String,
    private val isNew: Boolean,
    private val repository: CampsiteRepository = CampsiteRepository(),
) {
    var campsite by mutableStateOf<Campsite?>(null)
        private set
    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        if (isNew) {
            this.campsite = Campsite(
                cid = cid,
                ownerId = "" // retrieve owner id from Auth database here
            )
        } else {
            repository.getCampsite(
                cid = cid
            ) { exist, campsite ->
                if (exist) {
                    this.campsite = campsite
                }
            }
        }
    }

    /** Called by the view when the user taps Save. */
    fun save(
        name: String,
        address: String,
        description: String,
        rulesAndPolicy: String,
        cancellationPolicy: String,
        tags: List<String>,
        maxOccupancy: String,
        onSave: (String) -> Unit,
    ) {
        val c = campsite ?: return

        if (name.isBlank() || address.isBlank()) {
            errorMessage = "Name and address are required."
            return
        }
        val maxOccupancy = maxOccupancy.toIntOrNull()
        if (maxOccupancy == null || maxOccupancy <= 0) {
            errorMessage = "Maximum occupancy must be a whole number greater than 0."
            return
        }

        // Update the model, then hand it to the repository.
        // ratingAverage is deliberately not touched: it is computed from entrant ratings.
        c.name = name.trim()
        c.address = address.trim()
        c.description = description.trim()
        c.rulesAndPolicy = rulesAndPolicy.trim()
        c.cancellationPolicy = cancellationPolicy.trim()
        c.tags = tags
        c.maxOccupancy = maxOccupancy

        errorMessage = null
        if (isNew) {
            repository.addCampsite(
                campsite = c
            ) { success, msg ->
                if (success) {
                    onSave("Campsite registered!")
                } else {
                    onSave("Failed to register campsite: $msg")
                }
            }
        } else {
            repository.updateCampsite(
                campsite = c
            ) { success, msg ->
                if (success) {
                    onSave("Changes applied!")
                } else {
                    onSave("Failed to apply changes: $msg")
                }
            }
        }
    }
}
