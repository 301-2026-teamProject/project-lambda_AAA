package com.example.lambda_aaa.model

import kotlin.random.Random

object LotteryDrawer {
    fun selectWinners(entries: List<LotteryEntry>, numWinners: Int): Pair<List<LotteryEntry>, List<LotteryEntry>> {
        val waiting = entries.filter { it.status == EntryStatus.WAITING }
        val shuffled = waiting.shuffled(Random(System.currentTimeMillis()))
        val winners = shuffled.take(numWinners)
        val nonWinners = shuffled.drop(numWinners)
        val selectedWinners = winners.map { it.copy(status = EntryStatus.SELECTED) }
        val selectedNonWinners = nonWinners.map { it.copy(status = EntryStatus.NOT_SELECTED) }
        return selectedWinners to selectedNonWinners
    }
}
