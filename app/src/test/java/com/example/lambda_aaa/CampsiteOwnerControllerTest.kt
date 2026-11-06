package com.example.lambda_aaa

import com.example.lambda_aaa.controller.CampsiteOwnerController
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.CampsiteRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

/** Covers US 02.03.01 (view a list of my campsites).
 * A mock CampsiteRepository repo is provided so no Firestore integration is required for this test.
 */
class CampsiteOwnerControllerTest {

    private lateinit var repo: CampsiteRepository

    private val a = Campsite(cid = "a", ownerId = "u1", name = "Alpha")
    private val b = Campsite(cid = "b", ownerId = "u1", name = "Bravo")

    @Before
    fun setUp() {
        repo = mockk(relaxed = true)
    }

    private fun stubOwnerQuery(uid: String, ok: Boolean, list: List<Campsite>, msg: String? = null) {
        every { repo.getOwnerCampsites(eq(uid), any()) } answers {
            secondArg<(Boolean, List<Campsite>, String?) -> Unit>().invoke(ok, list, msg)
        }
    }

    @Test
    fun init_queriesRepositoryForThisOwnerOnly() {
        stubOwnerQuery("u1", true, listOf(a))

        CampsiteOwnerController("u1", repo)

        verify(exactly = 1) { repo.getOwnerCampsites(eq("u1"), any()) }
        verify(exactly = 0) { repo.getAllCampsites(any()) }
    }

    @Test
    fun init_isLoadingUntilRepositoryResponds() {
        val cb = slot<(Boolean, List<Campsite>, String?) -> Unit>()
        every { repo.getOwnerCampsites(eq("u1"), capture(cb)) } returns Unit

        val c = CampsiteOwnerController("u1", repo)
        assertTrue(c.isLoading)
        assertTrue(c.campsites.isEmpty())

        cb.captured.invoke(true, listOf(a, b), null)
        assertFalse(c.isLoading)
        assertEquals(listOf(a, b), c.campsites)
    }

    @Test
    fun success_exposesCampsitesAndNoError() {
        stubOwnerQuery("u1", true, listOf(a, b))

        val c = CampsiteOwnerController("u1", repo)

        assertEquals(listOf(a, b), c.campsites)
        assertNull(c.errorMessage)
        assertFalse(c.isLoading)
    }

    @Test
    fun success_withNoCampsites_givesEmptyListAndNoError() {
        stubOwnerQuery("u1", true, emptyList())

        val c = CampsiteOwnerController("u1", repo)

        assertTrue(c.campsites.isEmpty())
        assertNull(c.errorMessage)
        assertFalse(c.isLoading)
    }

    @Test
    fun failure_setsErrorMessageAndStopsLoading() {
        stubOwnerQuery("u1", false, emptyList(), "offline")

        val c = CampsiteOwnerController("u1", repo)

        assertEquals("Failed to load campsites: offline", c.errorMessage)
        assertTrue(c.campsites.isEmpty())
        assertFalse(c.isLoading)
    }

    @Test
    fun refresh_queriesAgainAndReplacesList() {
        stubOwnerQuery("u1", true, listOf(a))
        val c = CampsiteOwnerController("u1", repo)

        stubOwnerQuery("u1", true, listOf(a, b))
        c.refresh()

        assertEquals(listOf(a, b), c.campsites)
        verify(exactly = 2) { repo.getOwnerCampsites(eq("u1"), any()) }
    }

    @Test
    fun fetchCampsite_returnsMatchingCampsiteFromLoadedList() {
        stubOwnerQuery("u1", true, listOf(a, b))
        val c = CampsiteOwnerController("u1", repo)

        assertEquals(b, c.fetchCampsite("b"))
    }

    @Test
    fun fetchCampsite_unknownId_returnsNull() {
        stubOwnerQuery("u1", true, listOf(a))
        val c = CampsiteOwnerController("u1", repo)

        assertNull(c.fetchCampsite("zzz"))
    }

    @Test
    fun differentOwners_getIndependentLists() {
        val other = Campsite(cid = "x", ownerId = "u2")
        stubOwnerQuery("u1", true, listOf(a))
        stubOwnerQuery("u2", true, listOf(other))

        val c1 = CampsiteOwnerController("u1", repo)
        val c2 = CampsiteOwnerController("u2", repo)

        assertEquals(listOf(a), c1.campsites)
        assertEquals(listOf(other), c2.campsites)
    }

    @Test
    fun refresh_afterFailure_clearsErrorOnSuccess() {
        stubOwnerQuery("u1", false, emptyList(), "offline")
        val c = CampsiteOwnerController("u1", repo)

        stubOwnerQuery("u1", true, listOf(a))
        c.refresh()

        assertNull(c.errorMessage)
    }

    @Test
    fun refresh_setsLoadingWhileWaiting() {
        stubOwnerQuery("u1", true, listOf(a))
        val c = CampsiteOwnerController("u1", repo)

        every { repo.getOwnerCampsites(eq("u1"), any()) } returns Unit
        c.refresh()

        assertTrue(c.isLoading)
    }
}
