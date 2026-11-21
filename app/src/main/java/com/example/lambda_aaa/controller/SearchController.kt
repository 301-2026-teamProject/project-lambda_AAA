package com.example.lambda_aaa.controller

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.CampsiteRepository
import com.example.lambda_aaa.model.UserProfile
import com.example.lambda_aaa.model.UserRepository
import java.time.LocalDate

class SearchController(
    private val campsiteRepository: CampsiteRepository,
    private val userRepository: UserRepository? = null
    //private val lotteryRepository: LotteryRepository
) {
    var campsites by mutableStateOf(listOf<Campsite>())
    var filteredCampsites by mutableStateOf(listOf<Campsite>())
    //var lotteries by mutableStateOf(listOf<Lottery>())
    var profiles by mutableStateOf(listOf<UserProfile>())
    var filteredProfiles by mutableStateOf(listOf<UserProfile>())
    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init { refresh() }

    fun refresh() {
        fetchCampsites()
        filteredCampsites = campsites

        fetchProfiles()
        filteredProfiles = profiles

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

    fun fetchCampsiteNum(): Int {
        return campsites.size
    }

    fun fetchProfiles() {
        userRepository?.getAllProfiles { success, profiles, msg ->
            if (success) {
                this.profiles = profiles
            } else {
                errorMessage = "Failed to load profiles: $msg"
            }
            isLoading = false
        }
    }

    fun fetchProfileNum(): Int {
        return profiles.size
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
        searchQuery: String = "",
        selectedLocation: String?,
        selectedGuestCount: Int?,
        startDate: LocalDate?,
        endDate: LocalDate?
    ) {
        filteredCampsites = campsites.filter { campsite ->
            val matchSearch = (searchQuery == "" || campsite.name.lowercase().contains(searchQuery.lowercase())) // Checks if the searchbar is empty, or if a campsite has part of its text
            val matchesLocation = (selectedLocation == null || campsite.address == selectedLocation) // Checks if a campsite location matches the searched one
            val matchesGuests = (selectedGuestCount == null || selectedGuestCount <= campsite.maxOccupancy) // Checks if an inputted occupancy count is less than the campsite one
            val matchesDates = (startDate == null || endDate == null ||  (!campsite.availablePeriod.start.isAfter(startDate)) && (campsite.availablePeriod.end.isBefore(endDate)))
            matchSearch && matchesLocation && matchesGuests && matchesDates
        }
    }

    fun filterProfiles(
        searchQuery: String = "",
    ) {
        filteredProfiles = profiles.filter { profile ->
            val matchSearch = (searchQuery == "" || profile.name.lowercase().contains(searchQuery.lowercase())) // Checks if the searchbar is empty, or if a profile has part of its text
            matchSearch
        }
    }
}