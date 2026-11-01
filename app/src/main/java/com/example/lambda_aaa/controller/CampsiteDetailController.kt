package com.example.lambda_aaa.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.CampsiteRepository
import com.example.lambda_aaa.model.Lottery
import com.example.lambda_aaa.model.LotteryRepository

class CampsiteDetailController(
    private val cid: String,
    private val repository: CampsiteRepository
) {
    var campsite by mutableStateOf<Campsite?>(null)
    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    var lotteries by mutableStateOf<List<Lottery>>(emptyList())
        private set
    var lotteriesLoading by mutableStateOf(false)
        private set
    var lotteriesFailed by mutableStateOf(false)
        private set
    private var campsiteRequest = 0
    private var lotteryRequest = 0

    fun fetchCampsiteAndLottery() {
        val request = ++campsiteRequest
        ++lotteryRequest // Ignore a previous lottery response after a reload
        lotteries = emptyList()
        lotteriesLoading = false
        lotteriesFailed = false
        isLoading = true
        errorMessage = null
        campsite = null
        repository.getCampsite(cid = cid) { success, campsite, _ ->
            if (request != campsiteRequest) return@getCampsite
            if (success && campsite != null) {
                this.campsite = campsite
                fetchLotteries()
            } else {
                errorMessage = if (success) "Campsite does not exist."
                    else "Could not load campsite. Check your connection and try again."
            }
            isLoading = false
        }
    }

    // Keep campsite details visible if the independent lottery request fails
    fun fetchLotteries() {
        if (campsite == null) return
        val request = ++lotteryRequest
        lotteriesLoading = true
        lotteriesFailed = false
        lotteries = emptyList()
        sharedLotteryRepository.getAllLotteries { success, result, _ ->
            if (request != lotteryRequest) return@getAllLotteries
            lotteries = if (success) result.filter { it.campsite.cid == cid }
                .sortedBy { it.bookingPeriod?.start } else emptyList()
            lotteriesFailed = !success
            lotteriesLoading = false
        }
    }

    private companion object {
        //
        val sharedLotteryRepository by lazy { LotteryRepository() }
    }
}
