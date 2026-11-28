package com.example.lambda_aaa.controller

import com.example.lambda_aaa.model.CampsiteRepository
import com.example.lambda_aaa.model.Rating
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * CampsiteRepository is a final class that touches Firebase in its init block; mockk<T>() creates
 * the mock without running that constructor.
 */
class AdminRatingControllerTest {

    private val cid = "campsite-1"
    private lateinit var repo: CampsiteRepository

    @Before
    fun setUp() {
        repo = mockk()
    }

    private fun rating(rid: String, stars: Int = 4) =
        Rating(name = "User $rid", rid = rid, stars = stars, feedback = "feedback $rid")

    private fun stubRatings(success: Boolean, list: List<Rating> = emptyList(), msg: String? = null) {
        every { repo.getCampsiteRatings(cid, any()) } answers {
            secondArg<(Boolean, List<Rating>, String?) -> Unit>().invoke(success, list, msg)
        }
    }

    private fun stubRemove(success: Boolean, msg: String? = null) {
        every { repo.removeCampsiteRatings(cid, any(), any()) } answers {
            thirdArg<(Boolean, String?) -> Unit>().invoke(success, msg)
        }
    }

    @Test
    fun init_loadsRatingsForTheGivenCampsite() {
        stubRatings(true, listOf(rating("r1"), rating("r2", 2)))

        val controller = AdminRatingController(cid, repo)

        verify(exactly = 1) { repo.getCampsiteRatings(cid, any()) }
        assertEquals(listOf("r1", "r2"), controller.ratings.map { it.rid })
        assertFalse(controller.isLoading)
        assertNull(controller.errorMessage)
    }

    @Test
    fun init_isLoadingUntilRepositoryResponds() {
        val callback = slot<(Boolean, List<Rating>, String?) -> Unit>()
        every { repo.getCampsiteRatings(cid, capture(callback)) } returns Unit

        val controller = AdminRatingController(cid, repo)

        assertTrue(controller.isLoading)
        assertTrue(controller.ratings.isEmpty())

        callback.captured.invoke(true, listOf(rating("r1")), null)

        assertFalse(controller.isLoading)
        assertEquals(1, controller.ratings.size)
    }

    @Test
    fun init_noRatings_givesEmptyListWithoutError() {
        stubRatings(true, emptyList())

        val controller = AdminRatingController(cid, repo)

        assertTrue(controller.ratings.isEmpty())
        assertNull(controller.errorMessage)
        assertFalse(controller.isLoading)
    }

    @Test
    fun init_failure_setsErrorMessage() {
        stubRatings(false, msg = "unavailable")

        val controller = AdminRatingController(cid, repo)

        assertEquals("Failed to load ratings: unavailable", controller.errorMessage)
        assertTrue(controller.ratings.isEmpty())
        assertFalse(controller.isLoading)
    }

    @Test
    fun refresh_clearsPreviousErrorAndShowsNewRatings() {
        stubRatings(false, msg = "offline")
        val controller = AdminRatingController(cid, repo)
        assertEquals("Failed to load ratings: offline", controller.errorMessage)

        stubRatings(true, listOf(rating("r9")))
        controller.refresh()

        assertNull(controller.errorMessage)
        assertEquals(listOf("r9"), controller.ratings.map { it.rid })
        verify(exactly = 2) { repo.getCampsiteRatings(cid, any()) }
    }

    @Test
    fun refresh_setsLoadingWhileInFlight() {
        stubRatings(true, listOf(rating("r1")))
        val controller = AdminRatingController(cid, repo)

        val callback = slot<(Boolean, List<Rating>, String?) -> Unit>()
        every { repo.getCampsiteRatings(cid, capture(callback)) } returns Unit
        controller.refresh()

        assertTrue(controller.isLoading)
        assertNull(controller.errorMessage)
    }

    @Test
    fun removeRating_success_forwardsCampsiteAndRatingIdAndReportsSuccess() {
        stubRatings(true, listOf(rating("r1")))
        stubRemove(true)
        val controller = AdminRatingController(cid, repo)

        var result: Pair<Boolean, String?>? = null
        controller.removeRating("r1") { ok, msg -> result = ok to msg }

        verify(exactly = 1) { repo.removeCampsiteRatings(cid, "r1", any()) }
        assertEquals(true to null, result)
    }

    @Test
    fun removeRating_failure_reportsErrorMessage() {
        stubRatings(true, listOf(rating("r1")))
        stubRemove(false, "not found")
        val controller = AdminRatingController(cid, repo)

        var result: Pair<Boolean, String?>? = null
        controller.removeRating("r1") { ok, msg -> result = ok to msg }

        assertEquals(false to "not found", result)
    }

    /**
     * removeRating does not update the local list itself; the view calls refresh() (note the view
     * currently calls refresh() *before* removing, which is why the removed rating can linger).
     */
    @Test
    fun removeRating_doesNotChangeLocalListUntilRefreshed() {
        stubRatings(true, listOf(rating("r1"), rating("r2")))
        stubRemove(true)
        val controller = AdminRatingController(cid, repo)

        controller.removeRating("r1") { _, _ -> }
        assertEquals(listOf("r1", "r2"), controller.ratings.map { it.rid })

        stubRatings(true, listOf(rating("r2")))
        controller.refresh()
        assertEquals(listOf("r2"), controller.ratings.map { it.rid })
    }
}
