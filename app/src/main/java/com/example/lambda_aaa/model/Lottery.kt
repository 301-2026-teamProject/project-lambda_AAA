package com.example.lambda_aaa.model

import java.time.LocalDate
import java.util.UUID
import com.example.lambda_aaa.model.Campsite

class Lottery(
    val campsite: Campsite,
    val name: String,
    var bookingPeriod: DateRange? = null,
    var registrationPeriod: DateRange? = null,
    var numWinners: Int = 0,
    var waitlistCapacity: Int = 0,
    val id: String = UUID.randomUUID().toString()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "campsiteID" to campsite.cid,
        "name" to name,
        "bookingPeriodStart" to bookingPeriod?.start?.toString(),
        "bookingPeriodEnd" to bookingPeriod?.end?.toString(),
        "registrationPeriodStart" to registrationPeriod?.start?.toString(),
        "registrationPeriodEnd" to registrationPeriod?.end?.toString(),
        "numWinners" to numWinners,
        "waitlistCapacity" to waitlistCapacity
    )
}

data class LotteryForm(
    val campsite: Campsite? = null,
    val name: String = "",
    val regStart: LocalDate? = null,
    val regEnd: LocalDate? = null,
    val bookStart: LocalDate? = null,
    val bookEnd: LocalDate? = null,
    val winnersText: String = "",
    val capacityText: String = ""
) {
    val numWinners: Int? get() = winnersText.toIntOrNull()
    val waitlistCapacity: Int? get() = capacityText.toIntOrNull()

    fun toLotteryOrNull(): Lottery? {
        val winners = numWinners
        val capacity = waitlistCapacity

        if (campsite == null || name.isBlank() ||
            regStart == null || regEnd == null ||
            bookStart == null || bookEnd == null ||
            winners == null || capacity == null
        ) return null

        if (!regStart.isBefore(regEnd)) return null
        if (!bookStart.isBefore(bookEnd)) return null
        if (!regEnd.isBefore(bookStart)) return null
        if (winners >= capacity) return null

        return Lottery(
            campsite = campsite,
            name = name.trim(),
            registrationPeriod = DateRange(regStart, regEnd),
            bookingPeriod = DateRange(bookStart, bookEnd),
            numWinners = winners,
            waitlistCapacity = capacity
        )
    }
}