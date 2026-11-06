package com.example.lambda_aaa

import com.example.lambda_aaa.model.Lottery
import org.junit.Assert.*
import org.junit.Test

class LotteryMappingTest {
    private fun fields() = mapOf<String, Any?>("campsiteID" to "pine", "name" to "May lottery",
        "numWinners" to 2L, "waitlistCapacity" to 10L,
        "bookingPeriodStart" to "2027-05-01", "bookingPeriodEnd" to "2027-05-03")

    @Test fun preservesDocumentIdNameAndFirestoreLongCounts() {
        val lottery = Lottery.fromMap("saved-id", fields() + ("id" to "stale-field-id"))
        assertEquals("saved-id", lottery.id)
        assertEquals("May lottery", lottery.name)
        assertEquals(10, lottery.waitlistCapacity)
        assertEquals(2, lottery.numWinners)
        assertEquals("2027-05-01", lottery.bookingPeriod!!.start.toString())
    }
    @Test fun oldDocumentsWithoutNameOrCapacityStillLoad() {
        val lottery = Lottery.fromMap("old", fields() - "name" - "waitlistCapacity")
        assertEquals("", lottery.name)
        assertEquals(0, lottery.waitlistCapacity)
    }
    @Test(expected = IllegalArgumentException::class)
    fun incompleteDateRangeIsRejected() {
        Lottery.fromMap("bad", fields() - "bookingPeriodEnd")
    }
    @Test(expected = IllegalArgumentException::class)
    fun negativeCapacityIsRejected() {
        Lottery.fromMap("bad", fields() + ("waitlistCapacity" to -1L))
    }
    @Test(expected = IllegalArgumentException::class)
    fun fractionalWinnerCountIsRejected() {
        Lottery.fromMap("bad", fields() + ("numWinners" to 1.5))
    }
    @Test fun nestedLegacyPeriodsLoad() {
        val lottery = Lottery.fromMap("old", (fields() - "bookingPeriodStart" - "bookingPeriodEnd") +
            ("bookingPeriod" to mapOf("start" to "2027-05-01", "end" to "2027-05-03")))
        assertEquals("2027-05-03", lottery.bookingPeriod!!.end.toString())
    }
}
