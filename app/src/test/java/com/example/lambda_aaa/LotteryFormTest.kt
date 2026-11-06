package com.example.lambda_aaa

import com.example.lambda_aaa.model.LotteryForm
import java.time.LocalDate
import org.junit.Test
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull

class LotteryFormTest {
    private fun validForm() = LotteryForm(
        campsiteID = "c1",
        name = "May weekend lottery",
        regStart = LocalDate.of(2027, 5, 1),
        regEnd = LocalDate.of(2027, 5, 7),
        bookStart = LocalDate.of(2027, 5, 14),
        bookEnd = LocalDate.of(2027, 5, 31),
        winnersText = "5",
        capacityText = "50"
    )

    @Test
    fun validForm_createsLotteryWithCorrectFields() {
        val lottery = validForm().toLotteryOrNull()
        assertNotNull(lottery)
        lottery!!
        assertEquals("c1", lottery.campsiteID)
        assertEquals("May weekend lottery", lottery.name)
        assertEquals(5, lottery.numWinners)
        assertEquals(50, lottery.waitlistCapacity)
        assertEquals(LocalDate.of(2027, 5, 1), lottery.registrationPeriod?.start)
        assertEquals(LocalDate.of(2027, 5, 7), lottery.registrationPeriod?.end)
        assertEquals(LocalDate.of(2027, 5, 14), lottery.bookingPeriod?.start)
        assertEquals(LocalDate.of(2027, 5, 31), lottery.bookingPeriod?.end)
    }

    // Missing campsite / name
    @Test fun missingCampsite_rejected() =
        assertNull(validForm().copy(campsiteID = null).toLotteryOrNull())

    @Test fun blankName_rejected() =
        assertNull(validForm().copy(name = "   ").toLotteryOrNull())

    @Test fun missingCampsiteAndName_rejected() =
        assertNull(validForm().copy(campsiteID = null, name = "").toLotteryOrNull())

    // Winners vs capacity
    @Test fun winnersEqualToCapacity_rejected() =
        assertNull(validForm().copy(winnersText = "50", capacityText = "50").toLotteryOrNull())

    @Test fun winnersGreaterThanCapacity_rejected() =
        assertNull(validForm().copy(winnersText = "60", capacityText = "50").toLotteryOrNull())

    // Non-integer input
    @Test
    fun nonIntegerWinners_rejected() {
        val form = validForm().copy(winnersText = "abc")
        assertNull(form.numWinners)
        assertNull(form.toLotteryOrNull())
    }

    @Test fun decimalCapacity_rejected() =
        assertNull(validForm().copy(capacityText = "5.5").toLotteryOrNull())

    @Test fun emptyNumbers_rejected() =
        assertNull(validForm().copy(winnersText = "", capacityText = "").toLotteryOrNull())

    // Extra: date rules
    @Test fun missingDate_rejected() =
        assertNull(validForm().copy(regEnd = null).toLotteryOrNull())

    @Test fun registrationStartAfterEnd_rejected() =
        assertNull(validForm().copy(regStart = LocalDate.of(2027, 5, 8)).toLotteryOrNull())

    @Test fun bookingStartSameAsEnd_rejected() =
        assertNull(validForm().copy(bookEnd = LocalDate.of(2027, 5, 14)).toLotteryOrNull())

    @Test fun registrationEndsAfterBookingStarts_rejected() =
        assertNull(validForm().copy(regEnd = LocalDate.of(2027, 5, 20)).toLotteryOrNull())

    @Test fun registrationEndsOnBookingStartDay_rejected() =
        assertNull(validForm().copy(regEnd = LocalDate.of(2027, 5, 14)).toLotteryOrNull())

    // Extra: name is cleaned up
    @Test fun name_isTrimmed() =
        assertEquals("Lottery", validForm().copy(name = "  Lottery  ").toLotteryOrNull()?.name)
}
