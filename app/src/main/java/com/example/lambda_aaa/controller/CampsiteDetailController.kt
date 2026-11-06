package com.example.lambda_aaa.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.lambda_aaa.model.*
import com.example.lambda_aaa.model.detail.CampsiteDetailDataSource
import com.example.lambda_aaa.model.detail.FirestoreCampsiteDetailDataSource
import java.time.LocalDate

enum class LotteryJoinPhase { CHECKING, READY, JOINING, JOINED, NOT_OPEN, CLOSED, UNAVAILABLE, FULL, ERROR, FINISHED }
data class LotteryJoinState(val phase: LotteryJoinPhase, val message: String? = null)

/**
 * Controller for retrieving data from CampsiteRepository into views
 *
 * Bridges view and repository so that views can get data from CampsiteRepository but never has to touch it. Also
 * contains loading indicators during data extraction period.
 *
 * @property cid campsite id
 * @property data methods for accessing and retrieving data from database
 * @property today date of the current system time
 * @property campsite stores campsite session-persistent data
 * @property isLoading stores campsite loading status
 * @property errorMessage stores error message
 * @property lotteries stores lottery of the campsite
 * @property lotteriesLoading stores lottery loading status
 * @property joinStates store the join state of a given lottery id
 * @property identityLoading store loading status of device-based identification
 * @property identityFailed true if identification fails
 * @property entrantId stores id of the entrant after identifyEntrant() method
 * @property lotteryRequest stores number of lottery request
 * @property identityRequest stores number of identification request
 * @property entryRequests stores map from entrant id to request count
 * @property pendingJoins stores pending entrant id
 * @property lastDate stores current date of system time
 */
class CampsiteDetailController(
    private val cid: String,
    private val data: CampsiteDetailDataSource,
    private val today: () -> LocalDate = { LocalDate.now() },
) {
    constructor(cid: String, repository: CampsiteRepository) :
        this(cid, FirestoreCampsiteDetailDataSource(repository))

    var campsite by mutableStateOf<Campsite?>(null)
        private set
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
    var joinStates by mutableStateOf<Map<String, LotteryJoinState>>(emptyMap())
        private set
    var identityLoading by mutableStateOf(false)
        private set
    var identityFailed by mutableStateOf(false)
        private set
    private var entrantId: String? = null
    private var entrantEnabled = false
    private var campsiteRequest = 0
    private var lotteryRequest = 0
    private var identityRequest = 0
    private val entryRequests = mutableMapOf<String, Int>()
    private val pendingJoins = mutableSetOf<String>()
    private var lastDate = today()


    /**
     * fetch campsite; actually this method doesn't retrieve lottery since the function is delegated
     * to another method fetchLotteries(), but changing the name of this function will affect other
     * functions and will be a lot of work.
     *
     * @return the campsite if query match; null otherwise
     */
    fun fetchCampsiteAndLottery() {
        val request = ++campsiteRequest
        ++lotteryRequest
        entryRequests.clear()
        lotteries = emptyList()
        lotteriesLoading = false
        lotteriesFailed = false
        joinStates = emptyMap()
        isLoading = true
        errorMessage = null
        campsite = null
        data.getCampsite(cid) { success, loaded, _ ->
            if (request != campsiteRequest) return@getCampsite
            if (success && loaded != null) {
                campsite = loaded
                fetchLotteries()
            } else errorMessage = if (success) "Campsite does not exist."
                else "Could not load campsite. Check your connection and try again."
            isLoading = false
        }
    }


    /**
     * fetch lottery given the campsite cid
     *
     * @return return the lottery of that campsite if query matches; null otherwise
     */
    fun fetchLotteries() {
        val selected = campsite ?: return
        val request = ++lotteryRequest
        entryRequests.clear()
        lotteriesLoading = true
        lotteriesFailed = false
        data.getLotteries(selected) { success, result, _ ->
            if (request != lotteryRequest) return@getLotteries
            lotteries = if (success) result.filter { it.campsiteID == cid }
                .sortedBy { it.bookingPeriod?.start } else emptyList()
            lotteriesFailed = !success
            lotteriesLoading = false
            joinStates = emptyMap()
            if (entrantEnabled) lotteries.forEach { refreshEntry(it.id) }
        }
    }


    /**
     * get the entrant id
     *
     * @return entrant id if query matches; null otherwise
     */
    fun identifyEntrant() {
        entrantEnabled = true
        val request = ++identityRequest
        identityLoading = true
        identityFailed = false
        entrantId = null
        data.getEntrantId { success, id, _ ->
            if (request != identityRequest) return@getEntrantId
            entrantId = id?.takeIf { success && it.isNotBlank() }
            identityLoading = false
            identityFailed = entrantId == null
            if (!identityFailed) lotteries.forEach { refreshEntry(it.id) }
        }
    }


    /**
     * Check join eligibility of an entrant given the lottery
     *
     * @param lottery the given lottery of a campsite event
     * @return the join state of that lottery
     */
    private fun eligibility(lottery: Lottery): LotteryJoinPhase {
        val registration = lottery.registrationPeriod ?: return LotteryJoinPhase.UNAVAILABLE
        val booking = lottery.bookingPeriod ?: return LotteryJoinPhase.UNAVAILABLE
        if (lottery.numWinners <= 0 || !today().isBefore(booking.end)) return LotteryJoinPhase.UNAVAILABLE
        // Date only registration windows including their displayed end date
        return when {
            today().isBefore(registration.start) -> LotteryJoinPhase.NOT_OPEN
            today().isAfter(registration.end) -> LotteryJoinPhase.CLOSED
            else -> LotteryJoinPhase.READY
        }
    }


    /**
     * Set the state of the lottery
     *
     * @param id lottery id
     * @param phase the desired lottery phase after updating
     * @param message optional lottery update message
     */
    private fun setState(id: String, phase: LotteryJoinPhase, message: String? = null) {
        joinStates = joinStates + (id to LotteryJoinState(phase, message))
    }



    fun refreshEntry(id: String) {
        val lottery = lotteries.find { it.id == id } ?: return
        if (id in pendingJoins) { setState(id, LotteryJoinPhase.JOINING); return }
        val uid = entrantId ?: return
        val generation = lotteryRequest
        val request = (entryRequests[id] ?: 0) + 1
        entryRequests[id] = request
        setState(id, LotteryJoinPhase.CHECKING)
        data.getEntry(id, uid) { success, entry, _ ->
            if (generation != lotteryRequest || request != entryRequests[id] || uid != entrantId) return@getEntry
            when {
                !success -> setState(id, LotteryJoinPhase.ERROR, "Could not check your entry. Please retry.")
                entry?.status == EntryStatus.WAITING -> setState(id, LotteryJoinPhase.JOINED)
                entry != null && entry.status != EntryStatus.WITHDRAWN ->
                    setState(id, LotteryJoinPhase.FINISHED, "Lottery result: ${entry.status.name.lowercase().replace('_', ' ')}")
                else -> setState(id, eligibility(lottery))
            }
        }
    }



    // Recheck the registration window when time changes or before any write
    fun refreshEligibility() {
        if (today() != lastDate) {
            lastDate = today()
            if (entrantEnabled) lotteries.forEach { refreshEntry(it.id) }
        }
    }


    /**
     * join the lottery given lottery id and the device-based entrant identity
     *
     * @param id lottery id
     */
    fun joinLottery(id: String) {
        val uid = entrantId ?: return
        val selectedCampsite = campsite ?: return
        val lottery = lotteries.find { it.id == id } ?: return
        if (joinStates[id]?.phase != LotteryJoinPhase.READY || id in pendingJoins || lotteriesLoading) return
        val eligibility = eligibility(lottery)
        if (eligibility != LotteryJoinPhase.READY) { setState(id, eligibility); return }
        pendingJoins.add(id)
        setState(id, LotteryJoinPhase.JOINING)
        // The owner may have closed or removed the lottery since this screen loaded
        data.getLotteries(selectedCampsite) { loaded, latest, _ ->
            val current = latest.find { it.id == id && it.campsiteID == selectedCampsite.cid }
            if (!loaded) {
                pendingJoins.remove(id)
                setState(id, LotteryJoinPhase.ERROR, "Could not check the lottery. Please retry.")
            } else if (current == null) {
                pendingJoins.remove(id)
                setState(id, LotteryJoinPhase.UNAVAILABLE)
            } else {
                lotteries = lotteries.map { if (it.id == id) current else it }
                checkMembershipAndJoin(current, uid)
            }
        }
    }


    /**
     * Check membership of the entrant and joins them to the lottery
     *
     * @param lottery the given lottery of the campsite event
     * @param uid the entrant's user id
     */
    private fun checkMembershipAndJoin(lottery: Lottery, uid: String) {
        val id = lottery.id
        // Check membership once more so a known result is never reset to wating
        data.getEntry(id, uid) { checked, existing, _ ->
            if (!checked) {
                pendingJoins.remove(id)
                setState(id, LotteryJoinPhase.ERROR, "Could not check your entry. Please retry.")
            } else if (existing != null && existing.status != EntryStatus.WITHDRAWN) {
                pendingJoins.remove(id)
                refreshEntry(id)
            } else if (eligibility(lottery) != LotteryJoinPhase.READY) {
                pendingJoins.remove(id)
                setState(id, eligibility(lottery))
            } else {
                data.join(id, uid) { success, message ->
                    pendingJoins.remove(id)
                    when {
                        success -> setState(id, LotteryJoinPhase.JOINED, "You joined the waiting list.")
                        message == "Already joined." -> setState(id, LotteryJoinPhase.JOINED)
                        message == "Full." -> setState(id, LotteryJoinPhase.FULL)
                        else -> setState(id, LotteryJoinPhase.ERROR, message ?: "Could not join the lottery. Please retry.")
                    }
                }
            }
        }
    }

    /**
     * Withdraw entrant from the lottery event
     *
     * @param id lottery id
     */
    fun leaveLottery(id: String) {
        val uid = entrantId ?: return
        val lottery = lotteries.find { it.id == id } ?: return
        if (joinStates[id]?.phase != LotteryJoinPhase.JOINED || id in pendingJoins || lotteriesLoading) return
        pendingJoins.add(id)
        setState(id, LotteryJoinPhase.JOINING)
        data.leave(id, uid) { success, message ->
            pendingJoins.remove(id)
            if (success) {
                refreshEntry(id)
            } else {
                setState(id, LotteryJoinPhase.ERROR, message ?: "Could not leave the waitlist. Please retry.")
            }
        }
    }
}
