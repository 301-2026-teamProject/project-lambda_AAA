package com.example.lambda_aaa.model

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.util.Log


// create lottery screen uses the viewModel to save state of form
class LotteryViewModel(
    private val repository: LotteryRepository = LotteryRepository()
): ViewModel() {
    fun createLottery(lottery: Lottery, onResult: (Boolean, String?) -> Unit) {
        repository.create(lottery, onResult)
    }

    fun editLottery(lottery: Lottery, lotteryID: String, onResult: (Boolean, String?) -> Unit) {
        repository.updateLottery(lottery, lotteryID, onResult)
    }
}

class CreateLotteryViewModel(
    private val userRepo: UserRepository = UserRepository(),
    private val campsiteRepo: CampsiteRepository = CampsiteRepository()
) : ViewModel() {

    // Each collaborator's campsites, loaded once and cached
    var collaboratorCampsites by mutableStateOf<Map<String, List<Campsite>>>(emptyMap())
        private set
    var loadingOwnerIds by mutableStateOf<Set<String>>(emptySet())
        private set
    var error by mutableStateOf<String?>(null)
        private set

    var otherOwners by mutableStateOf<List<UserProfile>>(emptyList())
        private set
    var isLoadingOwners by mutableStateOf(false)
        private set

    fun loadOwners(currentOwnerId: String) {
        if (isLoadingOwners) return
        isLoadingOwners = true

        userRepo.getAllProfiles { ok, profiles, message ->
            Log.d("Owners", "ok=$ok, count=${profiles.size}, message=$message")
            profiles.forEach { Log.d("Owners", "id=${it.uid}, name=${it.name}, isOwner=${it.isOwner}") }
            isLoadingOwners = false
            if (ok) {
                otherOwners = profiles
                    .filter { it.isOwner && it.uid != currentOwnerId }
                    .sortedBy { it.name.lowercase() }
            } else {
                error = message ?: "Couldn't load owners"
            }
        }
    }

    fun loadCampsitesFor(ownerId: String) {
        if (ownerId in collaboratorCampsites || ownerId in loadingOwnerIds) return

        loadingOwnerIds = loadingOwnerIds + ownerId
        campsiteRepo.getOwnerCampsites(ownerId) { ok, campsites, message ->
            loadingOwnerIds = loadingOwnerIds - ownerId
            if (ok) collaboratorCampsites = collaboratorCampsites + (ownerId to campsites)
            else error = message ?: "Couldn't load collaborator's campsites"
        }
    }
}