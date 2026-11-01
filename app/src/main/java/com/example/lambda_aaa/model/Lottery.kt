package com.example.lambda_aaa.model

import java.time.LocalDate
import java.util.UUID

data class DateRange(val start: LocalDate, val end: LocalDate) {
    init {
        require(end.isAfter(start)) { "End date must be after start date" }
    }
}

class Lottery(
    val campsite: Campsite,
    var bookingPeriod: DateRange? = null,
    var registrationPeriod: DateRange? = null,
    var numWinners: Int = 0,
    val id: String = UUID.randomUUID().toString()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "campsiteID" to campsite.cid,
        "bookingPeriodStart" to bookingPeriod?.start?.toString(),
        "bookingPeriodEnd" to bookingPeriod?.end?.toString(),
        "registrationPeriodStart" to registrationPeriod?.start?.toString(),
        "registrationPeriodEnd" to registrationPeriod?.end?.toString(),
        "numWinners" to numWinners
    )
}