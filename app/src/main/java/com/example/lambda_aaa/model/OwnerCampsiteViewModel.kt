package com.example.lambda_aaa.model

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
data class OwnerCampsitesUiState(
    val isLoading: Boolean = true,
    val campsites: List<Campsite> = emptyList(),
    val error: String? = null
)

class OwnerCampsitesViewModel(
    private val campsiteRepo: CampsiteRepository = CampsiteRepository()
) : ViewModel() {

    var uiState by mutableStateOf(OwnerCampsitesUiState())
        private set

    fun load(ownerId: String) {
        uiState = uiState.copy(isLoading = true, error = null)
        campsiteRepo.getOwnerCampsites(ownerId) { ok, campsites, message ->
            uiState = if (ok) {
                OwnerCampsitesUiState(
                    isLoading = false,
                    campsites = campsites.sortedBy { it.name.lowercase() }
                )
            } else {
                uiState.copy(isLoading = false, error = message ?: "Couldn't load campsites")
            }
        }
    }
}