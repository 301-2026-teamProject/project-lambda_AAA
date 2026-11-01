package com.example.lambda_aaa

import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.foundation.layout.Column
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.ui.theme.CampsiteReservationTheme
import com.example.lambda_aaa.view.CampsiteDetailContent
import com.example.lambda_aaa.view.CampsiteCard
import com.google.firebase.firestore.GeoPoint
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.File

class CampsiteCardTest {
    @get:Rule val compose = createComposeRule()
    private val pine = Campsite(
        cid = "pine/with space", name = "Pine Ridge", address = "Jasper, Alberta",
        maxOccupancy = 8, ratingAverage = 4.7, ratingCount = 12,
        tags = listOf("Cookhouse", "Tennis", "Fire pit"),
        description = "Mountain group site beside the forest.",
        rulesAndPolicy = "Quiet hours at 10 PM.", cancellationPolicy = "48-hour cancellation.",
        mapLocation = GeoPoint(52.873, -118.082),
    )

    @Test fun cardOpensMatchingDetailsAndReturns() {
        val valley = pine.copy(cid = "valley", name = "Valley View", address = "Hinton, Alberta",
            maxOccupancy = 10, ratingCount = 0, mapLocation = null)
        compose.setContent {
            CampsiteReservationTheme {
                var selectedId by remember { mutableStateOf<String?>(null) }
                val campsites = listOf(pine, valley)
                val selected = campsites.find { it.cid == selectedId }
                if (selected == null) {
                    Column {
                        campsites.forEach { campsite ->
                            CampsiteCard(campsite, onClick = { selectedId = campsite.cid })
                        }
                    }
                } else {
                    CampsiteDetailContent(selected, false, null, {}, { selectedId = null })
                }
            }
        }
        compose.onNodeWithText("Jasper, Alberta", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Up to 8 guests", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Rating: 4.7 / 5", substring = true).assertIsDisplayed()
        compose.onNodeWithText("No ratings yet", substring = true).assertIsDisplayed()
        screenshot("story4-cards.png")
        compose.onNodeWithText("Pine Ridge", substring = true).performClick()
        compose.onNodeWithText("Pine Ridge").assertIsDisplayed()
        compose.onNodeWithText("Jasper, Alberta").assertIsDisplayed()
        compose.onNodeWithText("Rating: 4.7 / 5").assertIsDisplayed()
        compose.onNodeWithText("Edit campsite").assertDoesNotExist()
        screenshot("story5-details.png")
        compose.onNodeWithText("48-hour cancellation.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("View on map").performScrollTo().assertIsDisplayed()
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithText("Valley View", substring = true).performClick()
        compose.onNodeWithText("Hinton, Alberta").assertIsDisplayed()
        compose.onNodeWithText("No ratings yet").assertIsDisplayed()
        compose.onNodeWithText("View on map").assertDoesNotExist()
    }

    @Test fun missingOptionalFieldsAndEmptyLotteriesAreExplicit() {
        compose.setContent {
            CampsiteReservationTheme {
                CampsiteDetailContent(Campsite(name = "Unnamed area"), false, null, {}, {})
            }
        }
        compose.onNodeWithText("Location not provided").assertIsDisplayed()
        compose.onNodeWithText("Maximum occupancy not provided").assertIsDisplayed()
        compose.onNodeWithText("No ratings yet").assertIsDisplayed()
        compose.onAllNodesWithText("Not provided").assertCountEquals(3)
        compose.onNodeWithText("No lotteries listed for this campsite.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("View on map").assertDoesNotExist()
        compose.onNodeWithText("Edit campsite").assertDoesNotExist()
    }

    @Test fun failedLoadCanRetryAndShowsLoading() {
        var retries = 0
        compose.setContent {
            var loading by remember { mutableStateOf(false) }
            CampsiteReservationTheme {
                CampsiteDetailContent(null, loading, "Could not load campsite.", {
                    retries++
                    loading = true
                }, {})
            }
        }
        compose.onNodeWithText("Could not load campsite.").assertIsDisplayed()
        compose.onNodeWithText("Retry").performClick()
        compose.onNode(hasProgressBarRangeInfo(androidx.compose.ui.semantics.ProgressBarRangeInfo.Indeterminate)).assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, retries) }
        compose.onNodeWithText("Could not load campsite.").assertDoesNotExist()
    }

    @Test fun missingCampsiteShowsRetryInsteadOfEmptyDetails() {
        compose.setContent {
            CampsiteReservationTheme { CampsiteDetailContent(null, false, null, {}, {}) }
        }
        compose.onNodeWithText("Campsite does not exist.").assertIsDisplayed()
        compose.onNodeWithText("Retry").assertIsDisplayed()
        compose.onNodeWithText("Edit campsite").assertDoesNotExist()
    }

    @Test fun ownerStillHasEditAction() {
        var edits = 0
        compose.setContent {
            CampsiteReservationTheme { CampsiteDetailContent(pine, false, null, {}, {}, { edits++ }) }
        }
        compose.onNodeWithText("Edit campsite").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(1, edits) }
    }

    private fun screenshot(name: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        File(context.filesDir, name).outputStream().use {
            compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}
