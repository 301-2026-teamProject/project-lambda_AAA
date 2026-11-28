package com.example.lambda_aaa.model

import java.time.LocalDate

/**
 * Remaining stays within both availability periods, excluding past and booked nights.
 * Each range starts at check-in and ends at checkout, so adjacent stays are allowed.
 * [bookedDates] must contain only active reservations for this campsite.
 * This is an availability snapshot; booking must recheck availability atomically.
 */
fun availableReservationPeriods(
    campsitePeriod: DateRange,
    bookingPeriod: DateRange?,
    bookedDates: List<DateRange>,
    today: LocalDate = LocalDate.now()
): List<DateRange> {
    if (bookingPeriod == null) return emptyList()
    var nextAvailable = maxOf(campsitePeriod.start, bookingPeriod.start, today)
    val end = minOf(campsitePeriod.end, bookingPeriod.end)
    if (!nextAvailable.isBefore(end)) return emptyList()

    val available = mutableListOf<DateRange>()
    for (booked in bookedDates.sortedBy { it.start }) {
        if (!booked.end.isAfter(nextAvailable)) continue
        if (!booked.start.isBefore(end)) break
        if (booked.start.isAfter(nextAvailable)) {
            available.add(DateRange(nextAvailable, booked.start))
        }
        nextAvailable = maxOf(nextAvailable, booked.end)
        if (!nextAvailable.isBefore(end)) break
    }
    if (nextAvailable.isBefore(end)) available.add(DateRange(nextAvailable, end))
    return available
}
