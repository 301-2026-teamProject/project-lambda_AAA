package com.example.lambda_aaa.model

import java.time.LocalDate
import java.util.UUID

/**
 * Lotteries can be created by owners and joined by entrants. Requires at least one associated campsite
 * @property campsiteIDs list of campsite id strings
 * @property name name of the lottery
 * @property ownerIds list of owner ids
 * @property bookingPeriod period during which campsite can be booked
 * @property registrationPeriod period during which lottery can be joined
 * @property numWinners number of lottery winners
 * @property waitlistCapacity optional upper limit to number of entires into the lottery
 */

class Lottery(
    var campsiteIDs : List<String>,
    val name: String,
    val ownerIds: List<String> = emptyList(),
    var bookingPeriod: DateRange? = null,
    var registrationPeriod: DateRange? = null,
    var numWinners: Int,
    var waitlistCapacity: Int = 0,
    val id: String = UUID.randomUUID().toString()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "campsiteIDs" to campsiteIDs,
        "name" to name,
        "ownerIDs" to ownerIds,
        "bookingPeriodStart" to bookingPeriod?.start?.toString(),
        "bookingPeriodEnd" to bookingPeriod?.end?.toString(),
        "registrationPeriodStart" to registrationPeriod?.start?.toString(),
        "registrationPeriodEnd" to registrationPeriod?.end?.toString(),
        "numWinners" to numWinners,
        "waitlistCapacity" to waitlistCapacity
    )

    companion object {
        // converts FireStore JSON objects / fields into a lottery object
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
                campsiteIDs = fields["campsiteIDs"] as? List<String>
                    ?: throw IllegalArgumentException("Missing campsiteIDs"),
                name = fields["name"] as? String ?: "",
                ownerIds = (fields["ownerIDs"] as? List<*>)?.filterIsInstance<String>().orEmpty(),
                bookingPeriod = range("bookingPeriod"),
                registrationPeriod = range("registrationPeriod"),
                numWinners = count("numWinners"),
                waitlistCapacity = count("waitlistCapacity"),
                id = id
            )
        }
    }

    fun isRegistrationOpen(today: LocalDate = LocalDate.now()): Boolean {
        val period = registrationPeriod ?: return false
        return !today.isBefore(period.start) && !today.isAfter(period.end)
    }
}

/**
 * LotteryForm stores and validates data during createLotteryScreen
 * @property collaboratorIds ids of owners whose campsites may be used
 * @property campsiteIDs list of campsite ids
 * @property name name of campsite
 */
data class LotteryForm(
    val collaboratorIds: Set<String> = emptySet(),
    val campsiteIDs: List<String> = emptyList(),
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

    fun toggleCampsite(cid: String) = copy(
        campsiteIDs = if (cid in campsiteIDs) campsiteIDs - cid else campsiteIDs + cid
    )

    fun addCollaborator(uid: String) = copy(collaboratorIds = collaboratorIds + uid)

    // Removing a collaborator also deselects their campsites
    fun removeCollaborator(uid: String, theirCampsiteIds: Set<String>) = copy(
        collaboratorIds = collaboratorIds - uid,
        campsiteIDs = campsiteIDs - theirCampsiteIds
    )
    fun toLotteryOrNull(): Lottery? {
        val winners = numWinners
        val capacity = waitlistCapacity

        if (name.isBlank() || regStart == null || regEnd == null || bookStart == null || bookEnd == null || winners == null || capacity == null
        ) return null

        if (!regStart.isBefore(regEnd)) return null
        if (!bookStart.isBefore(bookEnd)) return null
        if (!regEnd.isBefore(bookStart)) return null
        if (capacity in 1..winners) return null

        return Lottery(
            campsiteIDs = campsiteIDs,
            name = name.trim(),
            registrationPeriod = DateRange(regStart, regEnd),
            bookingPeriod = DateRange(bookStart, bookEnd),
            numWinners = winners,
            waitlistCapacity = capacity
        )
    }
}