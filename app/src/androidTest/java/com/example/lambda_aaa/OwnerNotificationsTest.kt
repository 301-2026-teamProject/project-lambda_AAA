package com.example.lambda_aaa

import androidx.compose.runtime.mutableStateOf
import androidx.navigation.compose.rememberNavController
import com.example.lambda_aaa.view.LotteryWaitlistView
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lambda_aaa.model.*
import com.example.lambda_aaa.view.NotificationView
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.*
import org.junit.Assume.assumeNotNull
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class OwnerNotificationsTest {
    @get:Rule val compose = createComposeRule()

    @Test fun waitlistDeliveryRetryInboxAndSentDetails() {
        val port = InstrumentationRegistry.getArguments().getString("ownerNotificationEmulatorPort")?.toInt()
        assumeNotNull(port)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        FirebaseApp.getApps(context).firstOrNull { it.name == FirebaseApp.DEFAULT_APP_NAME }?.delete()
        FirebaseApp.initializeApp(context, FirebaseOptions.Builder().setProjectId("demo-owner-notifications")
            .setApplicationId("1:123:android:123abc").setApiKey("demo-api-key").build())
        val db = FirebaseFirestore.getInstance()
        db.useEmulator("127.0.0.1", port!!)
        val id = UUID.randomUUID().toString()
        val owner = "owner-$id"
        val repo = NotificationRepository()
        val message = "The trail is open."
        runBlocking { withTimeout(30_000) {
            db.collection("lotteries").document(id).set(mapOf("campsiteIDs" to listOf(id), "name" to "Pine")).await()
            db.collection("campsites").document(id).set(mapOf("ownerId" to owner)).await()
            for (name in listOf("First", "Second", "Muted", "Left", "First")) {
                db.collection("users").document(name + id)
                    .set(mapOf("name" to name, "notificationsEnabled" to (name != "Muted"))).await()
                db.collection("lottery_entries").add(mapOf("lotteryId" to id, "userId" to name + id,
                    "status" to if (name == "Left") "WITHDRAWN" else "WAITING")).await()
            }
            assertTrue(runCatching { repo.sendWaitlistBroadcast("another-owner", id, message, id) }
                .exceptionOrNull() is IllegalArgumentException)
            repeat(2) { assertEquals(2, repo.sendWaitlistBroadcast(owner, id, message, id)) }
            val deliveries = repo.sentBy(owner)
            assertEquals(2, deliveries.size)
            assertEquals(setOf("First$id", "Second$id"), deliveries.map { it.recipientId }.toSet())
            assertTrue(deliveries.all { it.timestamp != null && it.message == message })
            assertTrue(repo.sentBy("another-owner").isEmpty())
            val first = deliveries.first()
            db.collection("notifications").document(first.id).update("isRead", true).await()
            repo.sendWaitlistBroadcast(owner, id, message, id)
            assertTrue(repo.sentBy(owner).single { it.id == first.id }.isRead)
        }
        }
        val screen = mutableStateOf("waitlist")
        compose.setContent {
            when (screen.value) {
                "waitlist" -> LotteryWaitlistView(rememberNavController(), id, owner)
                "inbox" -> NotificationView(userId = "First$id", onBack = {})
                else -> NotificationView(userId = owner, showSent = true, onBack = {})
            }
        }
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("Notify waitlist"))
        compose.onNodeWithText("Notify waitlist").performClick()
        compose.onNode(hasSetTextAction()).performTextInput("Meet at the gate.")
        compose.onNodeWithText("Preview").performClick()
        compose.onNodeWithText("Meet at the gate.").assertIsDisplayed()
        compose.onNodeWithText("Send").performClick()
        compose.waitUntil(20_000) {
            runBlocking { repo.sentBy(owner).count { it.message == "Meet at the gate." } == 2 }
        }
        compose.runOnIdle { screen.value = "inbox" }
        compose.waitUntil(15_000) {
            compose.onAllNodesWithText("Meet at the gate.", substring = true).fetchSemanticsNodes().isNotEmpty()
        }
        compose.runOnIdle { screen.value = "sent" }
        compose.waitUntil(15_000) { compose.onAllNodesWithText(message).fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText(message).performClick()
        compose.onNodeWithText("First").assertIsDisplayed()
        compose.onNodeWithText("Second").assertIsDisplayed()
    }
}
