package com.example.lambda_aaa

import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.CampsiteTags
import com.example.lambda_aaa.model.DateRange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/** Plain-JVM tests for the campsite data models (US 02.01.02 data requirements). */
class CampsiteModelTest {

    // ---------- DateRange ----------

    @Test
    fun dateRange_endAfterStart_isValid() {
        val r = DateRange(LocalDate.of(2027, 5, 1), LocalDate.of(2027, 5, 3))
        assertEquals(LocalDate.of(2027, 5, 1), r.start)
        assertEquals(LocalDate.of(2027, 5, 3), r.end)
    }

    @Test
    fun dateRange_sameDay_isRejected() {
        val d = LocalDate.of(2027, 5, 1)
        assertThrows(IllegalArgumentException::class.java) { DateRange(d, d) }
    }

    @Test
    fun dateRange_endBeforeStart_isRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            DateRange(LocalDate.of(2027, 5, 3), LocalDate.of(2027, 5, 1))
        }
    }

    @Test
    fun dateRange_equalityIsByValue() {
        val a = DateRange(LocalDate.of(2027, 5, 1), LocalDate.of(2027, 5, 3))
        val b = DateRange(LocalDate.of(2027, 5, 1), LocalDate.of(2027, 5, 3))
        assertEquals(a, b)
    }

    // ---------- Campsite defaults ----------

    @Test
    fun campsite_defaults_areSafeEmptyValues() {
        val c = Campsite()
        assertEquals("", c.cid)
        assertEquals("", c.ownerId)
        assertEquals("", c.name)
        assertEquals("", c.address)
        assertNull(c.mapLocation)
        assertEquals(0, c.maxOccupancy)
        assertTrue(c.tags.isEmpty())
        assertNull(c.imageUrl)
        assertEquals(0.0, c.ratingAverage, 0.0)
        assertEquals(0, c.ratingCount)
        assertNull(c.lottery)
        assertNull(c.creationDate)
    }

    @Test
    fun campsite_defaultAvailablePeriod_isValidRange() {
        val c = Campsite()
        assertTrue(c.availablePeriod.end.isAfter(c.availablePeriod.start))
    }

    @Test
    fun campsite_isMutableForEditing() {
        val c = Campsite(cid = "c1", ownerId = "u1")
        c.name = "Pine Hollow"
        c.maxOccupancy = 8
        c.tags = listOf("Tent")
        assertEquals("Pine Hollow", c.name)
        assertEquals(8, c.maxOccupancy)
        assertEquals(listOf("Tent"), c.tags)
    }

    @Test
    fun campsite_copyDoesNotChangeOriginal() {
        val c = Campsite(cid = "c1", name = "A")
        val copy = c.copy(name = "B")
        assertEquals("A", c.name)
        assertEquals("B", copy.name)
        assertNotEquals(c, copy)
    }

    // ---------- CampsiteTags ----------

    @Test
    fun tags_haveNoDuplicatesOrBlanks() {
        val all = CampsiteTags.ALL
        assertEquals(all.size, all.distinct().size)
        assertTrue(all.none { it.isBlank() })
    }

    @Test
    fun tags_includeAccessibilityOption() {
        assertTrue("Wheelchair accessible" in CampsiteTags.ALL)
        assertFalse(CampsiteTags.ALL.isEmpty())
    }
}
