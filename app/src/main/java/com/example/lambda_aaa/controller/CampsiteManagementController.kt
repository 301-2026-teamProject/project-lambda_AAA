package com.example.lambda_aaa.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.CampsiteRepository
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import android.location.Geocoder
import java.util.Locale
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.example.lambda_aaa.model.EntryRepository
import com.example.lambda_aaa.model.Lottery
import com.example.lambda_aaa.model.LotteryEntry
import com.example.lambda_aaa.model.LotteryRepository
import java.io.IOException
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode

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
    private val context: Context,
) {
    var campsite by mutableStateOf<Campsite?>(null)
        private set
    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    var geoResult by mutableStateOf<Pair<String, GeoPoint?>>(Pair("", null))
        private set
    var isSearching by mutableStateOf(false)
        private set
    var geoSearchErrorMessage by mutableStateOf<String?>(null)
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
        mapLocation: GeoPoint?,
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
        c.mapLocation = mapLocation
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

    fun geoSearch(address: String) {
        isSearching = true
        geoSearchErrorMessage = null
        @Suppress("DEPRECATION")
        CoroutineScope(Dispatchers.IO).launch {
            val hit = try {
                Geocoder(context, Locale.getDefault())
                    .getFromLocationName(address, 1)?.firstOrNull()
            } catch (e: IOException) { null }
            withContext(Dispatchers.Main) {
                isSearching = false
                if (hit == null) geoSearchErrorMessage = "No matching location found."
                else geoResult = Pair(hit.getAddressLine(0) ?: address,
                    GeoPoint(hit.latitude, hit.longitude))
            }
        }
    }

    /** Calls each Repository to remove a Campsite, its link to any Lotteries, and the Lottery Entries to those Lotteries (if removed)
     *
     * @param campsite campsite name
     * @param onFail message to be returned if any step fails
     */
    fun deleteCampsite(
        campsite: Campsite,
        onFail: (String) -> Unit = {},
    ) {
        val lotteryRepo = LotteryRepository()
        val entryRepo = EntryRepository()

        var lotteryList: List<Lottery> = emptyList()
        lotteryRepo.getAllLotteries { check, lotteries, result ->
            if (check) {
                lotteryList = lotteries
            } else {
                onFail(result!!)
            }
        }

        // If we want to remove a campsite, we also have to remove all the related info
        for (lottery in lotteryList) { // We get rid of all references to the campsite in the lotteries
            if (lottery.campsiteIDs.contains(campsite.cid)) { // If any of the lotteries contains the campsite ID...
                lottery.campsiteIDs = lottery.campsiteIDs.filterNot {it == campsite.cid} // ... then we remove it
                lotteryRepo.updateLottery(lottery, lottery.id) { check, result -> if (!check) onFail(result!!) } // Updates lottery with removed campsite
            }
            if (lottery.campsiteIDs.isEmpty()) { // If lottery is empty we kill it
                var entries: List<LotteryEntry> = emptyList() // But first we have to remove all lottery entries
                entryRepo.getEntriesLottery(lottery.id) { check, listEntry, result ->
                    if (check) {
                        entries = listEntry // Gets all Lottery Entries that match the currently indexed lottery ID
                    } else {
                        onFail(result!!)
                    }
                }
                for (entry in entries) {
                    entryRepo.deleteEntry(entry) { check, result -> if (!check) onFail(result!!) } // Removes all entries
                }
                lotteryRepo.deleteLottery(lottery) { check, result -> if (!check) onFail(result!!) } // Then deletes the lottery
            }
        }
        lotteryRepo.close()
        repository.deleteCampsite(campsite) { check, result -> if (!check) onFail(result!!) }

    }
}