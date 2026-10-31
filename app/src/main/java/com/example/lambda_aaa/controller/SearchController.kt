package com.example.lambda_aaa.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.CampsiteRepository
import java.time.LocalDate

class SearchController(
    private val campsiteRepository: CampsiteRepository,
    //private val lotteryRepository: LotteryRepository
) {
    var campsites by mutableStateOf(listOf<Campsite>())
    var filteredCampsites by mutableStateOf(listOf<Campsite>())
    //var lotteries by mutableStateOf(listOf<Lottery>())
    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchCampsites()
        filteredCampsites = campsites
        //fetchLotteries()
    }

    fun fetchCampsites() {
        /* also get lottery of this campsite */
        campsiteRepository.getAllCampsites { success, campsites, msg ->
            if (success) {
                this.campsites = campsites
            } else {
                errorMessage = "Failed to load campsites: $msg"
            }
            isLoading = false
        }
    }
    /*
    fun fetchLotteries() {
        /* also get lottery of this campsite */
        lotteryRepository.getAllLotteries { success, lotteries, msg ->
            if (success) {
                this.lotteries = lotteries
            } else {
                errorMessage = "Failed to load campsites: $msg"
            }
            isLoading = false
        }
    }
    */
    fun filterCampsite(
        searchQuery: String,
        selectedLocation: String?,
        selectedGuestCount: Int?,
        startDate: String?,
        endDate: String?
    ) {
        filteredCampsites = campsites.filter { campsite ->
            val matchSearch = (searchQuery == "" || campsite.name.contains(searchQuery)) // Checks if the searchbar is empty, or if a campsite has part of its text
            val matchesLocation = (selectedLocation == null || campsite.address == selectedLocation) // Checks if a campsite location matches the searched one
            val matchesGuests = (selectedGuestCount == null || selectedGuestCount <= campsite.maxOccupancy) // Checks if an inputted occupancy count is less than the campsite one
            //val matchesDates = startDate == null || endDate == null ||  campsite.availableStartDate <= startDate && campsite.availableEndDate >= endDate
            matchSearch && matchesLocation && matchesGuests //&& matchesDates
        }
    }
}