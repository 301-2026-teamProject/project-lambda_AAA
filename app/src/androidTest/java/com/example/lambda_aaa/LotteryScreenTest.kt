package com.example.lambda_aaa

import androidx.compose.ui.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.view.CreateLotteryScreen
import androidx.compose.ui.test.junit4.v2.createComposeRule

class CreateLotteryScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val campsites = listOf(Campsite(cid = "c1", name = "Pine Ridge"))

    @Before
    fun setUp() {
        composeRule.setContent { CreateLotteryScreen(campsites, onClose = {}) }
    }

    @Test
    fun createButton_disabledWhenFormEmpty() {
        composeRule.onNodeWithTag("createLotteryButton").assertIsNotEnabled()
    }

    @Test
    fun nonIntegerInput_isRejected() {
        composeRule.onNodeWithTag("winnersField").performTextInput("abc")
        composeRule.onNodeWithTag("capacityField").performTextInput("50")
        // "abc" was blocked, so winners is empty and the capacity check stays hidden
        composeRule.onNodeWithText("Capacity check", substring = true).assertDoesNotExist()
    }

    @Test
    fun capacityCheck_showsCorrectText() {
        composeRule.onNodeWithTag("winnersField").performTextInput("5")
        composeRule.onNodeWithTag("capacityField").performTextInput("50")
        composeRule
            .onNodeWithText("Capacity check: 5 winner(s) from a maximum of 50 entrant(s)")
            .assertIsDisplayed()
    }

    @Test
    fun capacityCheck_showsWhenWinnersExceedCapacity() {
        composeRule.onNodeWithTag("winnersField").performTextInput("60")
        composeRule.onNodeWithTag("capacityField").performTextInput("50")
        composeRule.onNodeWithText("Capacity check", substring = true).assertIsDisplayed()
        composeRule.onNodeWithTag("createLotteryButton").assertIsNotEnabled()
    }

    @Test
    fun campsiteDropdown_selectsCampsite() {
        composeRule.onNodeWithText("Campsite").performClick()
        composeRule.onNodeWithText("Pine Ridge").performClick()
        composeRule.onNodeWithText("Pine Ridge").assertIsDisplayed()
    }
}