// Verifies that the application launches into the entrant dashboard.
package com.example.lambda_aaa

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun launchShowsEntrantDashboard() {
        composeRule.onNodeWithText("ENTRANT · DISCOVER").assertIsDisplayed()
        composeRule.onNodeWithText("Find your next campsite").assertIsDisplayed()
    }
}
