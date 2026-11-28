package com.example.lambda_aaa

import com.example.lambda_aaa.model.DateRange
import com.example.lambda_aaa.model.availableReservationPeriods
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class ReservationAvailabilityTest {
    private fun day(day: Int) = LocalDate.of(2027, 5, day)
    private fun range(start: Int, end: Int) = DateRange(day(start), day(end))

    @Test fun limitsDatesToBothCampsiteAndLottery() {
        assertEquals(listOf(range(5, 10)), availableReservationPeriods(
            range(1, 10), range(5, 15), emptyList(), day(1)))
        assertEquals(listOf(range(5, 10)), availableReservationPeriods(
            range(5, 15), range(1, 10), emptyList(), day(1)))
    }

    @Test fun missingBookingPeriodHasNoAvailability() {
        assertEquals(emptyList<DateRange>(), availableReservationPeriods(
            range(1, 10), null, emptyList(), day(1)))
    }

    @Test fun disjointOrTouchingPeriodsHaveNoBookableNights() {
        for (booking in listOf(range(10, 15), range(12, 15))) {
            assertEquals(emptyList<DateRange>(), availableReservationPeriods(
                range(1, 10), booking, emptyList(), day(1)))
        }
    }

    @Test fun removesPastNightsButAllowsCheckInToday() {
        assertEquals(listOf(range(6, 10)), availableReservationPeriods(
            range(1, 10), range(1, 10), emptyList(), day(6)))
    }

    @Test fun expiredPeriodHasNoAvailability() {
        assertEquals(emptyList<DateRange>(), availableReservationPeriods(
            range(1, 10), range(1, 10), emptyList(), day(10)))
    }

    @Test fun bookingSplitsAvailabilityAndAllowsSameDayTurnover() {
        assertEquals(listOf(range(1, 3), range(5, 10)), availableReservationPeriods(
            range(1, 10), range(1, 10), listOf(range(3, 5)), day(1)))
    }

    @Test fun unsortedOverlappingAndAdjacentBookingsLeaveOnlyRealGaps() {
        assertEquals(listOf(range(1, 3), range(9, 10)), availableReservationPeriods(
            range(1, 10), range(1, 10),
            listOf(range(7, 9), range(3, 6), range(5, 7), range(3, 6)), day(1)))
    }

    @Test fun bookingsOutsideTheWindowDoNotRemoveAvailableNights() {
        assertEquals(listOf(range(5, 10)), availableReservationPeriods(
            range(5, 10), range(5, 10), listOf(range(1, 5), range(10, 15)), day(1)))
    }

    @Test fun bookingsCrossingWindowBoundariesAreClipped() {
        assertEquals(listOf(range(6, 9)), availableReservationPeriods(
            range(5, 10), range(5, 10), listOf(range(1, 6), range(9, 15)), day(1)))
    }

    @Test fun fullyBookedWindowHasNoAvailability() {
        assertEquals(emptyList<DateRange>(), availableReservationPeriods(
            range(5, 10), range(5, 10), listOf(range(1, 15)), day(1)))
    }
}
