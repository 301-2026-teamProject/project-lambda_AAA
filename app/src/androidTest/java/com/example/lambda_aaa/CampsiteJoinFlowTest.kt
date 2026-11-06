package com.example.lambda_aaa

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lambda_aaa.controller.CampsiteDetailController
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.CampsiteRepository
import com.example.lambda_aaa.model.DateRange
import com.example.lambda_aaa.model.Lottery
import com.example.lambda_aaa.model.detail.FirestoreCampsiteDetailDataSource
import com.example.lambda_aaa.ui.theme.CampsiteReservationTheme
import com.example.lambda_aaa.view.CampsiteDetailView
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Source
import org.junit.*
import org.junit.Assert.*
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.TimeUnit

/** Real UI/controller/repository flow against a demo-project emulator, never production. */
class CampsiteJoinFlowTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var db: FirebaseFirestore
    private lateinit var campsites: CampsiteRepository
    private lateinit var source: FirestoreCampsiteDetailDataSource
    private val prefix = "details-test-${UUID.randomUUID()}"
    private val campsiteId = "$prefix-campsite"
    private val lotteryId = "$prefix-lottery"
    private val userId = "$prefix-user"
    private val deviceId = "$prefix-device"
    private val today = LocalDate.now()

    @Before fun prepare() {
        Assume.assumeTrue("Requires local Firestore emulator", InstrumentationRegistry.getArguments()
            .getString("firestoreEmulator") == "true")
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        FirebaseApp.getApps(context).find { it.name == FirebaseApp.DEFAULT_APP_NAME }?.delete()
        FirebaseApp.initializeApp(context, FirebaseOptions.Builder()
            .setProjectId("demo-lambda-details")
            .setApplicationId("1:1234567890:android:${UUID.randomUUID().toString().replace("-", "")}")
            .setApiKey("demo-api-key").build())
        db = FirebaseFirestore.getInstance()
        db.useEmulator("127.0.0.1", 8085)
        db.firestoreSettings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()
        assertEquals("demo-lambda-details", db.app.options.projectId)
        val campsite = Campsite(cid = campsiteId, name = "Integration Pine Ridge", address = "Jasper",
            maxOccupancy = 8, description = "Forest campsite", rulesAndPolicy = "Quiet after 10 PM",
            cancellationPolicy = "48 hours")
        val lottery = Lottery(campsite.cid, name = "testing",
            DateRange(today.plusDays(10), today.plusDays(12)),
            DateRange(today.minusDays(1), today.plusDays(1)), 2, waitlistCapacity = 30, lotteryId)
        Tasks.await(db.collection("campsites").document(campsiteId).set(campsite), 20, TimeUnit.SECONDS)
        Tasks.await(db.collection("lotteries").document(lotteryId).set(lottery.toMap()), 20, TimeUnit.SECONDS)
        Tasks.await(db.collection("users").document(userId).set(mapOf("userDeviceID" to deviceId)), 20, TimeUnit.SECONDS)
        campsites = CampsiteRepository()
        source = FirestoreCampsiteDetailDataSource(campsites, db, installationId = { Tasks.forResult(deviceId) })
    }

    @After fun cleanup() {
        if (!::db.isInitialized) return
        if (::campsites.isInitialized) campsites.close()
        val entries = Tasks.await(db.collection("lottery_entries").whereEqualTo("lotteryId", lotteryId)
            .get(Source.SERVER), 20, TimeUnit.SECONDS)
        val batch = db.batch()
        entries.documents.forEach { batch.delete(it.reference) }
        batch.delete(db.collection("campsites").document(campsiteId))
        batch.delete(db.collection("lotteries").document(lotteryId))
        batch.delete(db.collection("users").document(userId))
        Tasks.await(batch.commit(), 20, TimeUnit.SECONDS)
        Tasks.await(db.terminate(), 20, TimeUnit.SECONDS)
        db.app.delete()
    }

    @Test fun detailLoadsJoinsPersistsAndReloadsMembership() {
        val controller = CampsiteDetailController(campsiteId, source)
        var back = false
        compose.setContent {
            CampsiteReservationTheme { CampsiteDetailView(controller, onBack = { back = true }) }
        }
        compose.waitUntil(20_000) {
            controller.joinStates[lotteryId]?.phase == com.example.lambda_aaa.controller.LotteryJoinPhase.READY
        }
        compose.onNodeWithText("Integration Pine Ridge").assertIsDisplayed()
        compose.onNodeWithText("Booking: ${today.plusDays(10)} – ${today.plusDays(12)}")
            .performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Join lottery").performScrollTo().assertIsEnabled().performClick()
        compose.waitUntil(20_000) {
            controller.joinStates[lotteryId]?.phase == com.example.lambda_aaa.controller.LotteryJoinPhase.JOINED
        }
        compose.onNodeWithText("Already joined").assertIsNotEnabled()
        val saved = Tasks.await(db.collection("lottery_entries").whereEqualTo("lotteryId", lotteryId)
            .get(Source.SERVER), 20, TimeUnit.SECONDS)
        assertFalse(saved.metadata.isFromCache)
        assertEquals(1, saved.size())
        assertEquals(userId, saved.documents.single().getString("userId"))
        assertEquals("WAITING", saved.documents.single().getString("status"))
        val reloaded = CampsiteDetailController(campsiteId, source)
        compose.runOnIdle { reloaded.fetchCampsiteAndLottery(); reloaded.identifyEntrant() }
        compose.waitUntil(20_000) {
            reloaded.joinStates[lotteryId]?.phase == com.example.lambda_aaa.controller.LotteryJoinPhase.JOINED
        }
        compose.onNodeWithContentDescription("Back").performClick()
        compose.runOnIdle { assertTrue(back) }
    }

    @Test fun closedLotteryCannotBeJoined() {
        Tasks.await(db.collection("lotteries").document(lotteryId).update(mapOf(
            "registrationPeriodStart" to today.minusDays(5).toString(),
            "registrationPeriodEnd" to today.minusDays(1).toString())), 20, TimeUnit.SECONDS)
        val controller = CampsiteDetailController(campsiteId, source)
        compose.setContent {
            CampsiteReservationTheme { CampsiteDetailView(controller, onBack = {}) }
        }
        compose.waitUntil(20_000) {
            controller.joinStates[lotteryId]?.phase == com.example.lambda_aaa.controller.LotteryJoinPhase.CLOSED
        }
        compose.onNodeWithText("Registration is closed").performScrollTo().assertIsNotEnabled()
        val entries = Tasks.await(db.collection("lottery_entries").whereEqualTo("lotteryId", lotteryId)
            .get(Source.SERVER), 20, TimeUnit.SECONDS)
        assertTrue(entries.isEmpty)
    }
    @Test fun campsiteRepositoryRoundTripsNewFieldsAndAttachedLottery() {
        val original = Campsite(cid = campsiteId, ownerId = userId, name = "Availability test",
            pricePerNight = 65.0, availablePeriod = DateRange(today.plusDays(10), today.plusDays(20)))
        original.lottery = Lottery(original.cid, name="testing", DateRange(today.plusDays(10), today.plusDays(12)),
            DateRange(today.minusDays(1), today.plusDays(1)), 2, waitlistCapacity = 30, lotteryId)
        writeCampsite { campsites.addCampsite(original, it) }
        val stored = Tasks.await(db.collection("campsites").document(campsiteId).get(Source.SERVER), 20, TimeUnit.SECONDS)
        assertFalse(stored.metadata.isFromCache)
        assertEquals(today.plusDays(10).toString(), stored.getString("availablePeriod.start"))
        assertEquals(lotteryId, stored.getString("lottery.id"))
        assertNull(stored.get("lottery.campsite"))
        assertNotNull(stored.getTimestamp("creationDate"))
        val loaded = readCampsite()
        assertEquals(original.availablePeriod, loaded.availablePeriod)
        assertEquals(65.0, loaded.pricePerNight, 0.0)
        assertEquals(lotteryId, loaded.lottery!!.id)
        assertSame(loaded.cid, loaded.lottery!!.campsiteID)
        assertEquals(original.lottery!!.bookingPeriod, loaded.lottery!!.bookingPeriod)
        loaded.availablePeriod = DateRange(today.plusDays(11), today.plusDays(21))
        loaded.pricePerNight = 70.0
        writeCampsite { campsites.updateCampsite(loaded, it) }
        val reloaded = readCampsite()
        assertEquals(loaded.availablePeriod, reloaded.availablePeriod)
        assertEquals(70.0, reloaded.pricePerNight, 0.0)
        assertEquals(stored.getTimestamp("creationDate"), reloaded.creationDate)
        assertNotNull(reloaded.lastUpdatedDate)
        val all = TaskCompletionSource<List<Campsite>>()
        campsites.getAllCampsites { success, result, message ->
            if (success) all.setResult(result) else all.setException(IllegalStateException(message))
        }
        assertTrue(Tasks.await(all.task, 20, TimeUnit.SECONDS).any { it.cid == campsiteId })
        val owned = TaskCompletionSource<List<Campsite>>()
        campsites.getOwnerCampsites(userId) { success, result, message ->
            if (success) owned.setResult(result) else owned.setException(IllegalStateException(message))
        }
        assertEquals(campsiteId, Tasks.await(owned.task, 20, TimeUnit.SECONDS).single().cid)
        compose.waitUntil(20_000) { campsites.campsiteList.any { it.cid == campsiteId && it.pricePerNight == 70.0 } }
        writeCampsite { campsites.deleteCampsite(reloaded, it) }
        assertFalse(Tasks.await(db.collection("campsites").document(campsiteId).get(Source.SERVER), 20, TimeUnit.SECONDS).exists())
    }

    private fun writeCampsite(operation: ((Boolean, String?) -> Unit) -> Unit) {
        val completion = TaskCompletionSource<Boolean>()
        operation { success, message ->
            if (success) completion.setResult(true) else completion.setException(IllegalStateException(message))
        }
        Tasks.await(completion.task, 20, TimeUnit.SECONDS)
    }

    private fun readCampsite(): Campsite {
        val completion = TaskCompletionSource<Campsite>()
        campsites.getCampsite(campsiteId) { success, campsite, message ->
            if (success && campsite != null) completion.setResult(campsite)
            else completion.setException(IllegalStateException(message ?: "Campsite missing"))
        }
        return Tasks.await(completion.task, 20, TimeUnit.SECONDS)
    }

}
