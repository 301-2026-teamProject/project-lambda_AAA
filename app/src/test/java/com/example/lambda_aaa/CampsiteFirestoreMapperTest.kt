package com.example.lambda_aaa

import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.CampsiteFirestoreMapper
import com.example.lambda_aaa.model.DateRange
import com.example.lambda_aaa.model.Lottery
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class CampsiteFirestoreMapperTest {
    private val start = LocalDate.of(2027, 5, 1)
    private val end = LocalDate.of(2027, 5, 31)

    @Test fun availabilityPriceAndExistingFieldsSurviveRoundTrip() {
        val original = Campsite(cid = "pine", ownerId = "owner", name = "Pine", address = "Jasper",
            description = "Forest", maxOccupancy = 8, tags = listOf("Lake"), imageUrl = "image",
            rulesAndPolicy = "Quiet", cancellationPolicy = "48h", pricePerNight = 65.0,
            ratingAverage = 4.5, ratingCount = 2, availablePeriod = DateRange(start, end))
        val saved = CampsiteFirestoreMapper.encode(original)
        assertEquals(mapOf("start" to "2027-05-01", "end" to "2027-05-31"), saved["availablePeriod"])
        assertEquals(original, CampsiteFirestoreMapper.decode("pine", saved))
    }

    @Test fun linkedLotteryDoesNotSerializeCircularCampsite() {
        val original = Campsite(cid = "pine", availablePeriod = DateRange(start, end))
        original.lottery = Lottery(original.cid, name = "testing", DateRange(start, start.plusDays(2)),
            DateRange(start.minusDays(10), start.minusDays(1)), 3, waitlistCapacity = 30, "lottery-id")
        val fields = CampsiteFirestoreMapper.encode(original)
        val lotteryFields = fields["lottery"] as Map<*, *>
        assertFalse(lotteryFields.containsKey("campsite"))
        assertEquals("pine", lotteryFields["campsiteID"])
        val restored = CampsiteFirestoreMapper.decode("pine", fields)
        assertEquals("lottery-id", restored.lottery!!.id)
        assertEquals(original.lottery!!.bookingPeriod, restored.lottery!!.bookingPeriod)
        assertEquals(original.lottery!!.registrationPeriod, restored.lottery!!.registrationPeriod)
        assertSame(restored.cid, restored.lottery!!.campsiteID)
    }

    @Test fun readsPreviouslyAutomaticallySerializedLocalDates() {
        fun date(value: LocalDate) = mapOf("year" to value.year.toLong(),
            "monthValue" to value.monthValue.toLong(), "dayOfMonth" to value.dayOfMonth.toLong())
        val restored = CampsiteFirestoreMapper.decode("old", mapOf(
            "availablePeriod" to mapOf("start" to date(start), "end" to date(end))))
        assertEquals(DateRange(start, end), restored.availablePeriod)
    }

    @Test fun oldDocumentsWithoutNewFieldsRemainReadable() {
        val restored = CampsiteFirestoreMapper.decode("old", mapOf("name" to "Existing campsite"))
        assertEquals("old", restored.cid)
        assertEquals("Existing campsite", restored.name)
        assertEquals(0.0, restored.pricePerNight, 0.0)
        assertNull(restored.lottery)
    }

    @Test(expected = IllegalArgumentException::class)
    fun malformedAvailabilityIsNotReplacedWithPlausibleDates() {
        CampsiteFirestoreMapper.decode("bad", mapOf("availablePeriod" to mapOf("start" to start.toString())))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsLotteryForAnotherCampsite() {
        val campsite = Campsite(cid = "pine")
        campsite.lottery = Lottery(Campsite(cid = "other").cid, name = "testing")
        CampsiteFirestoreMapper.encode(campsite)
    }

    @Test(expected = IllegalArgumentException::class)
    fun attachedLotteryMustKeepItsSavedId() {
        CampsiteFirestoreMapper.decode("pine", mapOf("lottery" to mapOf("numWinners" to 1L)))
    }
    @Test fun readsPreviouslyEmbeddedLotteryWithNestedPeriods() {
        val restored = CampsiteFirestoreMapper.decode("pine", mapOf("lottery" to mapOf(
            "id" to "existing-lottery", "campsite" to mapOf("cid" to "pine"),
            "bookingPeriod" to mapOf("start" to start.toString(), "end" to end.toString()),
            "numWinners" to 2L)))
        assertEquals("existing-lottery", restored.lottery!!.id)
        assertEquals(DateRange(start, end), restored.lottery!!.bookingPeriod)
        assertSame(restored.cid, restored.lottery!!.campsiteID)
    }

}
