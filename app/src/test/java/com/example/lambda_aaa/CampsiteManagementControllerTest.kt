package com.example.lambda_aaa

import com.example.lambda_aaa.controller.CampsiteManagementController
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.CampsiteRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

/**
 * Covers US 02.01.01 (register), 02.01.02 (details + validation) and 02.01.03 (update).
 * This test provides a mock object of the campsite repository using mockk() method
 */
class CampsiteManagementControllerTest {

    private lateinit var repo: CampsiteRepository

    @Before
    fun setUp() {
        repo = mockk(relaxed = true)
    }

    // ---------- helpers ----------

    private fun stubGetCampsite(cid: String, ok: Boolean, campsite: Campsite?, msg: String? = null) {
        every { repo.getCampsite(eq(cid), any()) } answers {
            secondArg<(Boolean, Campsite?, String?) -> Unit>().invoke(ok, campsite, msg)
        }
    }

    private fun stubAdd(ok: Boolean, msg: String? = null) {
        every { repo.addCampsite(any(), any()) } answers {
            secondArg<(Boolean, String?) -> Unit>().invoke(ok, msg)
        }
    }

    private fun stubUpdate(ok: Boolean, msg: String? = null) {
        every { repo.updateCampsite(any(), any()) } answers {
            secondArg<(Boolean, String?) -> Unit>().invoke(ok, msg)
        }
    }

    private fun newController() = CampsiteManagementController("c1", "owner1", true, repo)

    private fun existingController(existing: Campsite = Campsite(cid = "c1", ownerId = "owner1", name = "Old")): CampsiteManagementController {
        stubGetCampsite("c1", true, existing)
        return CampsiteManagementController("c1", "owner1", false, repo)
    }

    private class SaveResult {
        var called = false
        var success: Boolean? = null
        var message: String? = null
        val callback: (Boolean, String) -> Unit = { s, m -> called = true; success = s; message = m }
    }

    private fun CampsiteManagementController.saveWith(
        result: SaveResult,
        name: String = "Pine Hollow",
        address: String = "Jasper, AB",
        description: String = "Cookhouse",
        rules: String = "Quiet hours 10pm",
        cancellation: String = "48h notice",
        tags: List<String> = listOf("Pet-friendly"),
        maxOccupancy: String = "8",
    ) = save(name, address, description, rules, cancellation, tags, maxOccupancy, result.callback)

    // ---------- init: new campsite (US 02.01.01) ----------

    @Test
    fun newCampsite_startsBlankWithGivenIdsAndNotLoading() {
        val c = newController()

        assertFalse(c.isLoading)
        assertNull(c.errorMessage)
        assertEquals("c1", c.campsite?.cid)
        assertEquals("owner1", c.campsite?.ownerId)
        assertEquals("", c.campsite?.name)
    }

    @Test
    fun newCampsite_doesNotReadFromRepository() {
        newController()
        verify(exactly = 0) { repo.getCampsite(any(), any()) }
    }

    // ---------- init: existing campsite (US 02.01.03) ----------

    @Test
    fun existingCampsite_loadSuccess_exposesCampsite() {
        val existing = Campsite(cid = "c1", ownerId = "owner1", name = "Old")
        val c = existingController(existing)

        assertFalse(c.isLoading)
        assertEquals(existing, c.campsite)
        assertNull(c.errorMessage)
    }

    @Test
    fun existingCampsite_loadFailure_setsErrorAndNoCampsite() {
        stubGetCampsite("c1", false, null, "offline")
        val c = CampsiteManagementController("c1", "owner1", false, repo)

        assertFalse(c.isLoading)
        assertNull(c.campsite)
        assertEquals("Failed to load campsite: offline", c.errorMessage)
    }

    @Test
    fun existingCampsite_missingDocument_setsError() {
        stubGetCampsite("c1", true, null)
        val c = CampsiteManagementController("c1", "owner1", false, repo)

        assertFalse(c.isLoading)
        assertNull(c.campsite)
        assertNotNull(c.errorMessage)
    }

    @Test
    fun existingCampsite_isLoadingUntilRepositoryResponds() {
        val cb = slot<(Boolean, Campsite?, String?) -> Unit>()
        every { repo.getCampsite(eq("c1"), capture(cb)) } returns Unit

        val c = CampsiteManagementController("c1", "owner1", false, repo)
        assertTrue(c.isLoading)

        cb.captured.invoke(true, Campsite(cid = "c1"), null)
        assertFalse(c.isLoading)
    }

    // ---------- validation (US 02.01.02) ----------

    @Test
    fun save_blankName_setsErrorAndDoesNotCallRepository() {
        val c = newController()
        val r = SaveResult()

        c.saveWith(r, name = "   ")

        assertEquals("Name and address are required.", c.errorMessage)
        assertFalse(r.called)
        verify(exactly = 0) { repo.addCampsite(any(), any()) }
    }

    @Test
    fun save_blankAddress_setsError() {
        val c = newController()
        val r = SaveResult()

        c.saveWith(r, address = "")

        assertEquals("Name and address are required.", c.errorMessage)
        assertFalse(r.called)
    }

    @Test
    fun save_invalidOccupancy_setsError() {
        listOf("", "0", "-3", "abc", "2.5").forEach { bad ->
            val c = newController()
            val r = SaveResult()

            c.saveWith(r, maxOccupancy = bad)

            assertEquals(
                "input '$bad'",
                "Maximum occupancy must be a whole number greater than 0.",
                c.errorMessage,
            )
            assertFalse(r.called)
            verify(exactly = 0) { repo.addCampsite(any(), any()) }
        }
    }

    @Test
    fun save_boundaryOccupancyOfOne_isAccepted() {
        stubAdd(true)
        val c = newController()
        val r = SaveResult()

        c.saveWith(r, maxOccupancy = "1")

        assertTrue(r.called)
        assertEquals(true, r.success)
        assertEquals(1, c.campsite?.maxOccupancy)
    }

    @Test
    fun save_validAfterValidationError_clearsErrorMessage() {
        stubAdd(true)
        val c = newController()
        val r = SaveResult()

        c.saveWith(r, name = "")
        assertNotNull(c.errorMessage)

        c.saveWith(r)
        assertNull(c.errorMessage)
    }

    // ---------- register (US 02.01.01) ----------

    @Test
    fun save_new_callsAddCampsiteWithEditedValuesAndReportsSuccess() {
        val added = slot<Campsite>()
        every { repo.addCampsite(capture(added), any()) } answers {
            secondArg<(Boolean, String?) -> Unit>().invoke(true, null)
        }
        val c = newController()
        val r = SaveResult()

        c.saveWith(r, tags = listOf("Showers", "Tent"))

        assertEquals(true, r.success)
        assertEquals("Campsite registered!", r.message)
        assertEquals("c1", added.captured.cid)
        assertEquals("owner1", added.captured.ownerId)
        assertEquals("Pine Hollow", added.captured.name)
        assertEquals("Jasper, AB", added.captured.address)
        assertEquals("Cookhouse", added.captured.description)
        assertEquals("Quiet hours 10pm", added.captured.rulesAndPolicy)
        assertEquals("48h notice", added.captured.cancellationPolicy)
        assertEquals(listOf("Showers", "Tent"), added.captured.tags)
        assertEquals(8, added.captured.maxOccupancy)
        verify(exactly = 0) { repo.updateCampsite(any(), any()) }
    }

    @Test
    fun save_trimsWhitespaceFromTextFields() {
        stubAdd(true)
        val c = newController()

        c.saveWith(
            SaveResult(),
            name = "  Pine Hollow  ", address = " Jasper ", description = " d ",
            rules = " r ", cancellation = " c ",
        )

        val saved = c.campsite!!
        assertEquals("Pine Hollow", saved.name)
        assertEquals("Jasper", saved.address)
        assertEquals("d", saved.description)
        assertEquals("r", saved.rulesAndPolicy)
        assertEquals("c", saved.cancellationPolicy)
    }

    @Test
    fun save_neverModifiesRatingFields() {
        stubAdd(true)
        val existing = Campsite(cid = "c1", ownerId = "owner1", ratingAverage = 4.5, ratingCount = 12)
        stubGetCampsite("c1", true, existing)
        stubUpdate(true)
        val c = CampsiteManagementController("c1", "owner1", false, repo)

        c.saveWith(SaveResult())

        assertEquals(4.5, c.campsite!!.ratingAverage, 0.0)
        assertEquals(12, c.campsite!!.ratingCount)
    }

    @Test
    fun save_new_repositoryFailure_reportsFailureMessage() {
        stubAdd(false, "boom")
        val c = newController()
        val r = SaveResult()

        c.saveWith(r)

        assertEquals(false, r.success)
        assertEquals("Failed to register campsite: boom", r.message)
    }

    // ---------- update (US 02.01.03) ----------

    @Test
    fun save_existing_callsUpdateNotAdd_andReportsSuccess() {
        stubUpdate(true)
        val c = existingController()
        val r = SaveResult()

        c.saveWith(r, name = "New Name")

        assertEquals(true, r.success)
        assertEquals("Changes applied!", r.message)
        assertEquals("New Name", c.campsite?.name)
        verify(exactly = 1) { repo.updateCampsite(any(), any()) }
        verify(exactly = 0) { repo.addCampsite(any(), any()) }
    }

    @Test
    fun save_existing_repositoryFailure_reportsFailureMessage() {
        stubUpdate(false, "denied")
        val c = existingController()
        val r = SaveResult()

        c.saveWith(r)

        assertEquals(false, r.success)
        assertEquals("Failed to apply changes: denied", r.message)
    }

    @Test
    fun save_beforeCampsiteLoaded_doesNothing() {
        stubGetCampsite("c1", false, null, "offline")
        val c = CampsiteManagementController("c1", "owner1", false, repo)
        val r = SaveResult()

        c.saveWith(r)

        assertFalse(r.called)
        verify(exactly = 0) { repo.updateCampsite(any(), any()) }
    }

    // US 02.01.03: owners cannot edit campsites they do not own
    @Test
    fun save_existing_byDifferentOwner_isRejected() {
        stubGetCampsite("c1", true, Campsite(cid = "c1", ownerId = "someoneElse"))
        stubUpdate(true)
        val c = CampsiteManagementController("c1", "owner1", false, repo)
        val r = SaveResult()

        c.saveWith(r)

        verify(exactly = 0) { repo.updateCampsite(any(), any()) }
        assertEquals(false, r.success)
    }
}
