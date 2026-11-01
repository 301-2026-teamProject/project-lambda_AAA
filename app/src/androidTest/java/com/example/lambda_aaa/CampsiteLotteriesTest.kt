package com.example.lambda_aaa

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.DateRange
import com.example.lambda_aaa.model.Lottery
import com.example.lambda_aaa.ui.theme.CampsiteReservationTheme
import com.example.lambda_aaa.view.CampsiteLotteriesSection
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class CampsiteLotteriesTest {
    @get:Rule val compose = createComposeRule()

    @Test fun displaysBookingRegistrationAndWinnerCountForMultipleLotteries() {
        val campsite = Campsite(cid = "pine", name = "Pine Ridge")
        val first = Lottery(campsite,
            bookingPeriod = DateRange(LocalDate.of(2026, 12, 10), LocalDate.of(2026, 12, 12)),
            registrationPeriod = DateRange(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30)),
            numWinners = 3)
        val second = Lottery(campsite, numWinners = 1)
        compose.setContent {
            CampsiteReservationTheme { CampsiteLotteriesSection(listOf(first, second), false, false, {}) }
        }
        compose.onNodeWithText("Lottery 1").assertIsDisplayed()
        compose.onNodeWithText("Booking: 2026-12-10 – 2026-12-12").assertIsDisplayed()
        compose.onNodeWithText("Registration: 2026-11-01 – 2026-11-30").assertIsDisplayed()
        compose.onNodeWithText("Winners: 3").assertIsDisplayed()
        compose.onNodeWithText("Lottery 2").assertIsDisplayed()
        compose.onNodeWithText("Booking dates not provided.").assertIsDisplayed()
        compose.onNodeWithText("Registration dates not provided.").assertIsDisplayed()
        compose.onNodeWithText("Winners: 1").assertIsDisplayed()
        compose.onNodeWithText("No lotteries listed for this campsite.").assertDoesNotExist()
    }

    @Test fun emptyResultIsDifferentFromLoading() {
        compose.setContent {
            CampsiteReservationTheme { CampsiteLotteriesSection(emptyList(), false, false, {}) }
        }
        compose.onNodeWithText("No lotteries listed for this campsite.").assertIsDisplayed()
        compose.onNodeWithText("Loading lotteries…").assertDoesNotExist()
        compose.onNodeWithText("Retry").assertDoesNotExist()
    }

    @Test fun loadingDoesNotClaimThereAreNoLotteries() {
        compose.setContent {
            CampsiteReservationTheme { CampsiteLotteriesSection(emptyList(), true, false, {}) }
        }
        compose.onNodeWithText("Loading lotteries…").assertIsDisplayed()
        compose.onNodeWithText("No lotteries listed for this campsite.").assertDoesNotExist()
    }

    @Test fun failedLoadOffersRetryInsteadOfEmptyResult() {
        var retries = 0
        compose.setContent {
            CampsiteReservationTheme { CampsiteLotteriesSection(emptyList(), false, true, { retries++ }) }
        }
        compose.onNodeWithText("Could not load lotteries. Please try again.").assertIsDisplayed()
        compose.onNodeWithText("No lotteries listed for this campsite.").assertDoesNotExist()
        compose.onNodeWithText("Retry").performClick()
        compose.runOnIdle { assertEquals(1, retries) }
    }
}
