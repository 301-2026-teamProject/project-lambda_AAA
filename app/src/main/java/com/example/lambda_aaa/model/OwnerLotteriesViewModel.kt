package com.example.lambda_aaa.model

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
data class OwnerLotteryItem(
    val lottery: Lottery,
    val campsiteNames: String
)

data class OwnerLotteriesUiState(
    val isLoading: Boolean = true,
    val lotteries: List<OwnerLotteryItem> = emptyList(),
    val ownedCampsites: List<Campsite> = emptyList(),
    val error: String? = null
)

class OwnerLotteriesViewModel(
    private val campsiteRepo: CampsiteRepository = CampsiteRepository(),
    private val lotteryRepo: LotteryRepository = LotteryRepository()
) : ViewModel() {

    var uiState by mutableStateOf(OwnerLotteriesUiState())
        private set

    fun load(ownerId: String) {
        uiState = uiState.copy(isLoading = true, error = null)

        campsiteRepo.getOwnerCampsites(ownerId) { campsitesOk, owned, campsitesMsg ->
            if (!campsitesOk) {
                uiState = uiState.copy(isLoading = false, error = campsitesMsg ?: "Couldn't load campsites")
                return@getOwnerCampsites
            }
            val campsiteById = owned.associateBy { it.cid }

            lotteryRepo.getAllLotteries { lotteriesOk, all, lotteriesMsg ->
                if (!lotteriesOk) {
                    uiState = uiState.copy(isLoading = false, error = lotteriesMsg ?: "Couldn't load lotteries")
                    return@getAllLotteries
                }

                val items = all
                    .filter { lottery ->
                        lottery.campsiteIDs.isNotEmpty() &&
                                lottery.campsiteIDs.all { it in campsiteById }
                    }
                    .map { lottery ->
                        OwnerLotteryItem(
                            lottery = lottery,
                            campsiteNames = lottery.campsiteIDs
                                .mapNotNull { campsiteById[it]?.name }
                                .joinToString(", ")
                        )
                    }
                    .sortedBy { it.lottery.name.lowercase() }

                uiState = OwnerLotteriesUiState(
                    isLoading = false,
                    lotteries = items,
                    ownedCampsites = owned
                )
            }
        }
    }
}