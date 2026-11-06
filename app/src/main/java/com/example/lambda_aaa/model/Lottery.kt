package com.example.lambda_aaa.model

import java.time.LocalDate
import java.util.UUID

// Lottery class is associated to a single (for now) campsite object through its campsiteID
// bookingPeriod and registrationPeriod are of type DateRange? which ensures start date is before end date
// all fields except campsite id and name are nullable as lottery should not exist without a campsite

class Lottery(
    val campsiteID : String,
    val name: String,
    var bookingPeriod: DateRange? = null,
    var registrationPeriod: DateRange? = null,
    var numWinners: Int = 0,
    var waitlistCapacity: Int = 0,
    val id: String = UUID.randomUUID().toString()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "campsiteID" to campsiteID,
        "name" to name,
        "bookingPeriodStart" to bookingPeriod?.start?.toString(),
        "bookingPeriodEnd" to bookingPeriod?.end?.toString(),
        "registrationPeriodStart" to registrationPeriod?.start?.toString(),
        "registrationPeriodEnd" to registrationPeriod?.end?.toString(),
        "numWinners" to numWinners,
        "waitlistCapacity" to waitlistCapacity
    )

    companion object {
        fun fromMap(id: String, fields: Map<String, Any?>): Lottery {
            require(id.isNotBlank()) { "Missing lottery ID" }
            fun date(raw: Any?): LocalDate = when (raw) {
                is String -> LocalDate.parse(raw)
                is Map<*, *> -> LocalDate.of((raw["year"] as Number).toInt(),
                    (raw["monthValue"] as Number).toInt(), (raw["dayOfMonth"] as Number).toInt())
                else -> throw IllegalArgumentException("Missing or invalid lottery date")
            }
            fun range(prefix: String): DateRange? {
                // Older update calls saved nested periods instead of the flat create schema.
                val nested = fields[prefix]
                require(nested == null || nested is Map<*, *>) { "Invalid $prefix" }
                val start = fields["${prefix}Start"] ?: (nested as? Map<*, *>)?.get("start")
                val end = fields["${prefix}End"] ?: (nested as? Map<*, *>)?.get("end")
                if (start == null && end == null) return null
                return DateRange(date(start), date(end))
            }
            fun count(field: String): Int {
                val raw = fields[field] ?: return 0
                require(raw is Number && raw.toLong() in 0..Int.MAX_VALUE.toLong()
                    && raw.toDouble() == raw.toLong().toDouble()) { "Invalid $field" }
                return raw.toInt()
            }

            return Lottery(
                campsiteID = fields["campsiteID"] as? String
                    ?: throw IllegalArgumentException("Missing campsiteID"),
                name = fields["name"] as? String ?: "",
                bookingPeriod = range("bookingPeriod"),
                registrationPeriod = range("registrationPeriod"),
                numWinners = count("numWinners"),
                waitlistCapacity = count("waitlistCapacity"),
                id = id
            )
        }
    }
}

data class LotteryForm(
    val campsiteID: String? = null,
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

        if (campsiteID == null || name.isBlank() ||
            regStart == null || regEnd == null ||
            bookStart == null || bookEnd == null ||
            winners == null || capacity == null
        ) return null

        if (!regStart.isBefore(regEnd)) return null
        if (!bookStart.isBefore(bookEnd)) return null
        if (!regEnd.isBefore(bookStart)) return null
        if (winners >= capacity) return null

        return Lottery(
            campsiteID = campsiteID,
            name = name.trim(),
            registrationPeriod = DateRange(regStart, regEnd),
            bookingPeriod = DateRange(bookStart, bookEnd),
            numWinners = winners,
            waitlistCapacity = capacity
        )
    }
}