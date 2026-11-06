package com.example.lambda_aaa

import com.example.lambda_aaa.controller.*
import com.example.lambda_aaa.model.*
import com.example.lambda_aaa.model.DateRange
import com.example.lambda_aaa.model.detail.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class CampsiteDetailControllerTest {
    private val date = LocalDate.of(2026, 11, 3)
    private val campsite = Campsite(cid = "pine", name = "Pine Ridge")
    private fun lottery(start: LocalDate = date.minusDays(1), end: LocalDate = date.plusDays(1)) =
        Lottery(campsite.cid, name = "", DateRange(date.plusDays(10), date.plusDays(12)), DateRange(start, end), 3, waitlistCapacity = 30, "lottery-real-id")

    private inner class Fake : CampsiteDetailDataSource {
        var available = listOf(lottery())
        var entry: LotteryEntry? = null
        var failLotteries = false
        var failEntry = false
        var failIdentity = false
        var missingCampsite = false
        var deferJoin = false
        var joinError: String? = null
        var writes = 0
        var writtenUid: String? = null
        var writtenLottery: String? = null
        var completion: ((Boolean, String?) -> Unit)? = null
        override fun getCampsite(cid: String, result: (Boolean, Campsite?, String?) -> Unit) = result(true, if (missingCampsite) null else campsite, null)
        override fun getLotteries(campsite: Campsite, result: (Boolean, List<Lottery>, String?) -> Unit) = result(!failLotteries, available, null)
        override fun getEntrantId(result: (Boolean, String?, String?) -> Unit) = result(!failIdentity, if (failIdentity) null else "saved-user-id", null)
        override fun getEntry(lotteryId: String, uid: String, result: (Boolean, LotteryEntry?, String?) -> Unit) = result(!failEntry, entry, null)
        override fun join(lotteryId: String, uid: String, result: (Boolean, String?) -> Unit) {
            writes++
            writtenUid = uid
            writtenLottery = lotteryId
            if (deferJoin) completion = result else result(joinError == null, joinError)
        }
        var leaves = 0
        var leftUid: String? = null
        var leftLottery: String? = null
        override fun leave(lotteryId: String, uid: String, result: (Boolean, String?) -> Unit) {
            leaves++
            leftUid = uid
            leftLottery = lotteryId
            result(true, null)
        }
    }
    private fun controller(fake: Fake, now: () -> LocalDate = { date }) =
        CampsiteDetailController("pine", fake, now).also { it.fetchCampsiteAndLottery(); it.identifyEntrant() }
    private fun phase(controller: CampsiteDetailController) = controller.joinStates["lottery-real-id"]?.phase

    @Test fun joinsUsingOriginalLotteryIdAndSavedUserId() {
        val source = Fake()
        val controller = controller(source)
        assertEquals(LotteryJoinPhase.READY, phase(controller))
        controller.joinLottery("lottery-real-id")
        assertEquals("saved-user-id", source.writtenUid)
        assertEquals("lottery-real-id", source.writtenLottery)
        assertEquals(LotteryJoinPhase.JOINED, phase(controller))
        controller.joinLottery("lottery-real-id")
        assertEquals(1, source.writes)
    }
    @Test fun repeatedTapsWhileJoiningWriteOnlyOnce() {
        val source = Fake().also { it.deferJoin = true }
        val controller = controller(source)
        repeat(3) { controller.joinLottery("lottery-real-id") }
        assertEquals(1, source.writes)
        assertEquals(LotteryJoinPhase.JOINING, phase(controller))
        source.completion!!(true, null)
        assertEquals(LotteryJoinPhase.JOINED, phase(controller))
    }
    @Test fun alreadyJoinedEntryNeverWritesAgain() {
        val source = Fake().also { it.entry = LotteryEntry(status = EntryStatus.WAITING) }
        val controller = controller(source)
        controller.joinLottery("lottery-real-id")
        assertEquals(LotteryJoinPhase.JOINED, phase(controller))
        assertEquals(0, source.writes)
    }
    @Test fun selectedEntryIsNotResetToWaiting() {
        val source = Fake().also { it.entry = LotteryEntry(status = EntryStatus.SELECTED) }
        val controller = controller(source)
        controller.joinLottery("lottery-real-id")
        assertEquals(LotteryJoinPhase.FINISHED, phase(controller))
        assertEquals(0, source.writes)
    }
    @Test fun resultArrivingAfterInitialCheckPreventsJoin() {
        val source = Fake()
        val controller = controller(source)
        source.entry = LotteryEntry(status = EntryStatus.BOOKED)
        controller.joinLottery("lottery-real-id")
        assertEquals(0, source.writes)
        assertEquals(LotteryJoinPhase.FINISHED, phase(controller))
    }
    @Test fun withdrawnEntryCanRejoin() {
        val source = Fake().also { it.entry = LotteryEntry(status = EntryStatus.WITHDRAWN) }
        val controller = controller(source)
        controller.joinLottery("lottery-real-id")
        assertEquals(1, source.writes)
    }
    @Test fun dateWindowIncludesEndDateAndBlocksAfterwards() {
        var now = date.plusDays(1)
        val source = Fake()
        val controller = controller(source) { now }
        assertEquals(LotteryJoinPhase.READY, phase(controller))
        now = date.plusDays(2)
        controller.joinLottery("lottery-real-id")
        assertEquals(LotteryJoinPhase.CLOSED, phase(controller))
        assertEquals(0, source.writes)
    }
    @Test fun futureRegistrationAndUnknownDatesCannotJoin() {
        val source = Fake().also { it.available = listOf(lottery(date.plusDays(1), date.plusDays(3))) }
        val controller = controller(source)
        assertEquals(LotteryJoinPhase.NOT_OPEN, phase(controller))
        source.available = listOf(lottery().also { it.registrationPeriod = null })
        controller.fetchLotteries()
        assertEquals(LotteryJoinPhase.UNAVAILABLE, phase(controller))
        controller.joinLottery("lottery-real-id")
        assertEquals(0, source.writes)
    }
    @Test fun failedIdentityPreventsWriteAndCanRetry() {
        val source = Fake().also { it.failIdentity = true }
        val controller = controller(source)
        controller.joinLottery("lottery-real-id")
        assertTrue(controller.identityFailed)
        assertEquals(0, source.writes)
        source.failIdentity = false
        controller.identifyEntrant()
        assertEquals(LotteryJoinPhase.READY, phase(controller))
    }
    @Test fun failedMembershipCheckRequiresSuccessfulRetry() {
        val source = Fake().also { it.failEntry = true }
        val controller = controller(source)
        controller.joinLottery("lottery-real-id")
        assertEquals(LotteryJoinPhase.ERROR, phase(controller))
        assertEquals(0, source.writes)
        source.failEntry = false
        controller.refreshEntry("lottery-real-id")
        assertEquals(LotteryJoinPhase.READY, phase(controller))
    }
    @Test fun failedJoinCanRetryWithoutClaimingSuccess() {
        val source = Fake().also { it.joinError = "Permission denied" }
        val controller = controller(source)
        controller.joinLottery("lottery-real-id")
        assertEquals(LotteryJoinPhase.ERROR, phase(controller))
        source.joinError = null
        controller.refreshEntry("lottery-real-id")
        controller.joinLottery("lottery-real-id")
        assertEquals(LotteryJoinPhase.JOINED, phase(controller))
    }
    @Test fun fullResponseIsShownAndCanBeCheckedAgain() {
        val source = Fake().also { it.joinError = "Full." }
        val controller = controller(source)
        controller.joinLottery("lottery-real-id")
        assertEquals(LotteryJoinPhase.FULL, phase(controller))
        controller.refreshEntry("lottery-real-id")
        assertEquals(LotteryJoinPhase.READY, phase(controller))
    }
    @Test fun lotteryFailureDoesNotHideCampsiteDetails() {
        val source = Fake().also { it.failLotteries = true }
        val controller = controller(source)
        assertEquals(campsite, controller.campsite)
        assertNull(controller.errorMessage)
        assertTrue(controller.lotteriesFailed)
        source.failLotteries = false
        controller.fetchLotteries()
        assertFalse(controller.lotteriesFailed)
    }
    @Test fun missingCampsiteDoesNotOfferJoin() {
        val controller = controller(Fake().also { it.missingCampsite = true })
        assertNotNull(controller.errorMessage)
        assertTrue(controller.lotteries.isEmpty())
    }
    @Test fun excludesLotteriesForOtherCampsites() {
        val source = Fake().also { it.available = listOf(lottery(), Lottery(Campsite(cid = "other").cid, name = "testing")) }
        assertEquals(listOf("lottery-real-id"), controller(source).lotteries.map { it.id })
    }
    @Test fun decodesTeammateFlatSchemaWithoutLosingDocumentId() {
        val original = lottery()
        val restored = FirestoreCampsiteDetailDataSource.decodeLottery("stored-id", original.toMap(), campsite)
        assertEquals("stored-id", restored.id)
        assertEquals(campsite.cid, restored.campsiteID)
        assertEquals(original.bookingPeriod, restored.bookingPeriod)
        assertEquals(original.registrationPeriod, restored.registrationPeriod)
        assertEquals(3, restored.numWinners)
    }
    @Test(expected = java.time.format.DateTimeParseException::class)
    fun malformedDatesAreRejectedInsteadOfEnablingJoin() {
        FirestoreCampsiteDetailDataSource.decodeLottery("stored-id", lottery().toMap() +
            ("registrationPeriodStart" to "not-a-date"), campsite)
    }
    @Test fun serverClosingLotteryAfterScreenLoadPreventsWrite() {
        val source = Fake()
        val controller = controller(source)
        source.available = listOf(lottery(date.minusDays(5), date.minusDays(1)))
        controller.joinLottery("lottery-real-id")
        assertEquals(0, source.writes)
        assertEquals(LotteryJoinPhase.CLOSED, phase(controller))
    }
    @Test fun deletedLotteryCannotBeJoinedFromStaleScreen() {
        val source = Fake()
        val controller = controller(source)
        source.available = emptyList()
        controller.joinLottery("lottery-real-id")
        assertEquals(0, source.writes)
        assertEquals(LotteryJoinPhase.UNAVAILABLE, phase(controller))
    }
    @Test fun leavesWaitlistSuccessfully() {
        val source = Fake().also { it.entry = LotteryEntry(status = EntryStatus.WAITING) }
        val controller = controller(source)
        assertEquals(LotteryJoinPhase.JOINED, phase(controller))
        controller.leaveLottery("lottery-real-id")
        assertEquals("saved-user-id", source.leftUid)
        assertEquals("lottery-real-id", source.leftLottery)
        assertEquals(1, source.leaves)
    }
}
