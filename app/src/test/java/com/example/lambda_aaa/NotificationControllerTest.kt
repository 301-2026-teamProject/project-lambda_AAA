package com.example.lambda_aaa

import com.example.lambda_aaa.controller.NotificationController
import com.example.lambda_aaa.model.*
import com.example.lambda_aaa.model.detail.NotificationDataSource
import org.junit.Assert.*
import org.junit.Test

class NotificationControllerTest {
    private class FakeNotificationDataSource : NotificationDataSource {
        val notifications = mutableListOf<AppNotification>()
        override fun saveNotification(notification: AppNotification, onResult: (Boolean, String?) -> Unit) {
            notifications.add(notification)
            onResult(true, null)
        }
        override fun getNotifications(recipientId: String, onResult: (Boolean, List<AppNotification>, String?) -> Unit) {
            onResult(true, notifications.filter { it.recipientId == recipientId }, null)
        }
        override fun markAsRead(notificationId: String, onResult: (Boolean, String?) -> Unit) {
            notifications.find { it.id == notificationId }?.isRead = true
            onResult(true, null)
        }
    }
    @Test
    fun loadLotteryWinnerAndLoserNotifications() {
        val dataSource = FakeNotificationDataSource()
        val controller = NotificationController(dataSource)
        dataSource.notifications.add(
            AppNotification(
                recipientId = "entrant-1",
                senderId = "system",
                lotteryId = "lottery-win",
                message = "You were selected! Choose an available date before booking.",
                type = NotificationType.WINNER,
                isRead = false
            )
        )
        dataSource.notifications.add(
            AppNotification(
                recipientId = "entrant-1",
                senderId = "system",
                lotteryId = "lottery-lose",
                message = "You were not selected.",
                type = NotificationType.NOT_SELECTED,
                isRead = false
            )
        )
        controller.loadNotifications("entrant-1") { list ->
            assertEquals(2, list.size)
            val winner = list.first { it.type == NotificationType.WINNER }
            val loser = list.first { it.type == NotificationType.NOT_SELECTED }
            assertEquals("You were selected! Choose an available date before booking.", winner.message)
            assertEquals("You were not selected.", loser.message)
            assertFalse(winner.isRead)
            assertFalse(loser.isRead)
            controller.markAsRead(winner.id) { success, _ ->
                assertTrue(success)
            }
            controller.loadNotifications("entrant-1") { updated ->
                val updatedWinner = updated.first { it.type == NotificationType.WINNER }
                assertTrue(updatedWinner.isRead)
            }
        }
    }
}
