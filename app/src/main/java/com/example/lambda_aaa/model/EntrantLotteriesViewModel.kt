package com.example.lambda_aaa.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
data class EntrantLotteriesUiState(
    val isLoading: Boolean = true,
    val lotteries: List<LotteryItem> = emptyList(),
    val error: String? = null
)

class EntrantLotteriesViewModel(
    private val campsiteRepo: CampsiteRepository = CampsiteRepository(),
    private val lotteryRepo: LotteryRepository = LotteryRepository()
) : ViewModel() {

    var uiState by mutableStateOf(EntrantLotteriesUiState())
        private set

    fun load() {
        uiState = uiState.copy(isLoading = true, error = null)

        lotteryRepo.getAllLotteries { ok, all, message ->
            if (!ok) {
                uiState = uiState.copy(isLoading = false, error = message ?: "Couldn't load lotteries")
                return@getAllLotteries
            }

            val open = all.filter { it.isRegistrationOpen() }
            val campsiteIds = open.flatMap { it.campsiteIDs }.distinct()

            if (campsiteIds.isEmpty()) {
                uiState = EntrantLotteriesUiState(isLoading = false, lotteries = emptyList())
                return@getAllLotteries
            }

            // Fetch each campsite once, then build the cards when the last one arrives
            val found = mutableMapOf<String, Campsite>()
            var remaining = campsiteIds.size

            campsiteIds.forEach { cid ->
                campsiteRepo.getCampsite(cid) { campsiteOk, campsite, _ ->
                    if (campsiteOk && campsite != null) found[cid] = campsite
                    remaining--

                    if (remaining == 0) {
                        val items = open
                            .map { lottery ->
                                LotteryItem(
                                    lottery = lottery,
                                    campsiteNames = lottery.campsiteIDs
                                        .mapNotNull { found[it]?.name }
                                        .joinToString(", ")
                                )
                            }
                            .sortedBy { it.lottery.name.lowercase() }

                        uiState = EntrantLotteriesUiState(isLoading = false, lotteries = items)
                    }
                }
            }
        }
    }
}