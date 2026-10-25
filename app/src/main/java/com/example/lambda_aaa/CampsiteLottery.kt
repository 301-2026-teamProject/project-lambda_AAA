package com.example.lambda_aaa

import com.example.lambda_aaa.model.Campsite
import java.time.LocalDate

data class DateRange(val start: LocalDate, val end: LocalDate) {
    init {
        require(end.isAfter(start)) { "End date must be on or after start date" }
    }
}

class CampsiteLottery(
    val campsite: Campsite,
    var period: DateRange? = null
) {
    fun updatePeriod(dateRange: DateRange) {
        period = dateRange
    }
}