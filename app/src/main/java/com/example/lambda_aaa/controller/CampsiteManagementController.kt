package com.example.lambda_aaa.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.CampsiteRepository

/**
 * loads the campsite by cid, and pushes the edited values
 * back through CampsiteRepository. The view never touches the repository.
 *
 * @property cid campsite id
 * @property uid user id; used to compared with updated campsite's ownerId
 * @property isNew true if the campsite is being created, false otherwise
 * @property repository contain reference to CampsiteRepository
 * @property campsite stores campsite data
 * @property errorMessage stores error message if error occurs; null otherwise
 */
class CampsiteManagementController(
    private val cid: String,
    private val uid: String,
    private val isNew: Boolean,
    private val repository: CampsiteRepository,
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
                ownerId = uid
            )
            isLoading = false
        } else {
            repository.getCampsite(
                cid = cid
            ) { exist, campsite, msg ->
                if (exist && campsite != null) {
                    this.campsite = campsite
                } else {
                    errorMessage = "Failed to load campsite: $msg"
                }
                isLoading = false
            }
        }
    }

    /** Called by the view when the user taps Save.
     *
     * @param name campsite name
     * @param address campsite address
     * @param description campsite description
     * @param rulesAndPolicy campsite rules and policy
     * @param cancellationPolicy campsite cancellation policies
     * @param tags campsite tags
     * @param maxOccupancy max occupancy for each campsite
     * @param onSave trigger app to return to main screen after saving
     */
    fun save(
        name: String,
        address: String,
        description: String,
        rulesAndPolicy: String,
        cancellationPolicy: String,
        tags: List<String>,
        maxOccupancy: String,
        onSave: (Boolean, String) -> Unit,
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
                    onSave(true, "Campsite registered!")
                } else {
                    onSave(false, "Failed to register campsite: $msg")
                }
            }
        } else {
            if (c.ownerId == uid) {
                repository.updateCampsite(
                    campsite = c
                ) { success, msg ->
                    if (success) {
                        onSave(true, "Changes applied!")
                    } else {
                        onSave(false, "Failed to apply changes: $msg")
                    }
                }
            } else {
                onSave(false, "Error: You are not the owner of that campsite.")
            }
        }
    }
}
