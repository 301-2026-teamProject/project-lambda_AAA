package com.example.lambda_aaa.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.lambda_aaa.model.CampsiteRepository
import com.example.lambda_aaa.model.MessageContact
import com.example.lambda_aaa.model.Rating
import com.example.lambda_aaa.model.NotificationType
import com.example.lambda_aaa.model.Role

/**
 * Loads the list of people the current user can message and sends messages to them.
 *
 * A message is stored as an AppNotification (type OWNER_MESSAGE or ENTRANT_MESSAGE), so it shows up
 * in the recipient's notification inbox. The view never touches a repository.
 *
 * @property cid campsite id of the clicked card
 * @property repository resolves contacts from lotteries, entries, campsites and users
 * @property ratings ratings related to the campsite of given cid
 * @property isLoading true while contacts are loading
 * @property errorMessage error text if loading failed; null otherwise
 */
class AdminRatingController(
    private val cid: String,
    private val repository: CampsiteRepository,
) {
    var ratings by mutableStateOf<List<Rating>>(emptyList())
        private set
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
        repository.getCampsiteRatings(campsiteId = cid) { success, ratingList, msg ->
            if (success) ratings = ratingList else errorMessage = "Failed to load ratings: $msg"
            isLoading = false
        }
    }

    /**
     * Communicates with database and remove rating given rid
     *
     * @param rid rating id
     * @param onResult what to do after result is produced
     */
    fun removeRating(
        rid: String,
        onResult: (Boolean, String?) -> Unit,
    ) {
        repository.removeCampsiteRatings(cid = cid, rid = rid) { success, msg ->
            onResult(success, msg)
        }
    }
}