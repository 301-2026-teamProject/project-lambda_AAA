package com.example.lambda_aaa

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import com.example.lambda_aaa.model.Lottery
import com.example.lambda_aaa.model.DateRange

class LotteryModelTest {

    @Test
    fun dateRange_endBeforeStart_throws() {
        assertThrows(IllegalArgumentException::class.java) {
            DateRange(LocalDate.of(2027, 5, 10), LocalDate.of(2027, 5, 1))
        }
    }

    @Test
    fun dateRange_sameDay_throws() {
        assertThrows(IllegalArgumentException::class.java) {
            DateRange(LocalDate.of(2027, 5, 10), LocalDate.of(2027, 5, 10))
        }
    }

    // toMap -> fromMap should give back the same lottery
    @Test
    fun toMap_fromMap_roundTrip() {
        val original = Lottery(
            campsiteID = "c1",
            name = "Test",
            bookingPeriod = DateRange(LocalDate.of(2027, 5, 14), LocalDate.of(2027, 5, 31)),
            registrationPeriod = DateRange(LocalDate.of(2027, 5, 1), LocalDate.of(2027, 5, 7)),
            numWinners = 5,
            waitlistCapacity = 50
        )
        val copy = Lottery.fromMap(original.id, original.toMap())

        assertEquals(original.id, copy.id)
        assertEquals(original.campsiteID, copy.campsiteID)
        assertEquals(original.name, copy.name)
        assertEquals(original.bookingPeriod, copy.bookingPeriod)
        assertEquals(original.registrationPeriod, copy.registrationPeriod)
        assertEquals(original.numWinners, copy.numWinners)
        assertEquals(original.waitlistCapacity, copy.waitlistCapacity)
    }

    @Test
    fun fromMap_missingCampsiteId_throws() {
        assertThrows(IllegalArgumentException::class.java) {
            Lottery.fromMap("id1", mapOf("name" to "Test"))
        }
    }

    @Test
    fun fromMap_missingDates_givesNullPeriods() {
        val lottery = Lottery.fromMap("id1", mapOf("campsiteID" to "c1", "name" to "Test"))
        assertNull(lottery.bookingPeriod)
        assertNull(lottery.registrationPeriod)
    }
}