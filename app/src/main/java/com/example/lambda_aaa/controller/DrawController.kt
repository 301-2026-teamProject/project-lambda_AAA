package com.example.lambda_aaa.controller

import com.example.lambda_aaa.model.*
import java.time.LocalDate

class DrawController(
    private val entryRepository: EntryRepository = EntryRepository(),
    private val notificationRepository: NotificationRepository = NotificationRepository(),
    private val lotteryRepository: LotteryRepository = LotteryRepository()
) {
    fun auto(lottery: Lottery, onResult: (Boolean, String?) -> Unit) {
        val end = lottery.registrationPeriod?.end ?: return onResult(false, "Registration period not found.")
        if (LocalDate.now().isAfter(end)) {
            draw(lottery) { success, msg -> onResult(success, msg) }
        } else {
            onResult(false, "Registration period has not ended.")
        }
    }
    fun draw(lottery: Lottery, onResult: (Boolean, String?) -> Unit) {
        entryRepository.getEntriesLottery(lottery.id) { success, entries, msg ->
            if (!success) {
                onResult(false, msg)
                return@getEntriesLottery
            }
            if (entries.any { it.status == EntryStatus.SELECTED || it.status == EntryStatus.NOT_SELECTED }) {
                onResult(true, "Draw already completed.")
                return@getEntriesLottery
            }
            val (winners, nonWinners) = LotteryDrawer.selectWinners(entries, lottery.numWinners)
            val updated = winners + nonWinners
            entryRepository.updateStatus(updated) { updateOk, updateMsg ->
                if (!updateOk) {
                    onResult(false, updateMsg)
                    return@updateStatus
                }
                val controller = NotificationController(notificationRepository)
                winners.forEach { winner ->
                    controller.sendNotification(
                        recipientId = winner.userId,
                        senderId = "system",
                        lotteryId = lottery.id,
                        message = "You were selected for ${lottery.name}!",
                        type = NotificationType.WINNER
                    ) { _, _ -> }
                }
                nonWinners.forEach { nonWinner ->
                    controller.sendNotification(
                        recipientId = nonWinner.userId,
                        senderId = "system",
                        lotteryId = lottery.id,
                        message = "You were not selected for ${lottery.name}.",
                        type = NotificationType.NOT_SELECTED
                    ) { _, _ -> }
                }
                onResult(true, "Draw completed.")
            }
        }
    }
}
