package com.example.lambda_aaa.model

import java.time.LocalDate
import java.util.UUID

/**
 * Possible statuses of a reservation
 */
enum class ReservationStatus { ACTIVE, CANCELLED, REJECTED }

/**
 * Created when an entrant books a campsite
 * @property campsiteId id of campsite
 * @property lotteryId id of lottery
 * @property entrantUid id of entrant
 * @property ownerId id of owner
 * @property period booking period of the reservation
 * @property status status of reservation (Default = active)
 */
class Reservation(
    val campsiteId: String,
    val lotteryId: String,
    val entrantUid: String,
    val ownerId: String,
    val period: DateRange,
    var status: ReservationStatus = ReservationStatus.ACTIVE,
    val id: String = UUID.randomUUID().toString()
) {
    // "Past" is calculated, never stored
    fun isPast(today: LocalDate = LocalDate.now()): Boolean =
        status == ReservationStatus.ACTIVE && period.end.isBefore(today)

    fun isActive(today: LocalDate = LocalDate.now()): Boolean =
        status == ReservationStatus.ACTIVE && !isPast(today)

    fun overlaps(other: DateRange): Boolean =
        period.start.isBefore(other.end) && other.start.isBefore(period.end)

    fun canBeRejected(today: LocalDate = LocalDate.now()): Boolean =
        status == ReservationStatus.ACTIVE && period.start.isAfter(today)

    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "campsiteID" to campsiteId,
        "lotteryID" to lotteryId,
        "entrantUID" to entrantUid,
        "ownerID" to ownerId,
        "start" to period.start.toString(),
        "end" to period.end.toString(),
        "status" to status.name
    )

    companion object {
        fun fromMap(id: String, fields: Map<String, Any?>): Reservation {
            fun string(key: String): String =
                fields[key] as? String ?: throw IllegalArgumentException("Missing $key")

            return Reservation(
                campsiteId = string("campsiteID"),
                lotteryId = string("lotteryID"),
                entrantUid = string("entrantUID"),
                ownerId = string("ownerID"),
                period = DateRange(LocalDate.parse(string("start")), LocalDate.parse(string("end"))),
                status = runCatching { ReservationStatus.valueOf(string("status")) }
                    .getOrDefault(ReservationStatus.ACTIVE),
                id = id
            )
        }
    }
}

/**
 * Stores different type of reservations for each owner based on reservation status and booking date
 */
data class OwnerReservations(
    val active: List<Reservation>,
    val past: List<Reservation>,
    val cancelled: List<Reservation>,
    val rejected: List<Reservation>
) {
    val all: List<Reservation> get() = active + past + cancelled + rejected
    companion object {
        fun from(all: List<Reservation>, today: LocalDate = LocalDate.now()) = OwnerReservations(
            active = all.filter { it.isActive(today) }.sortedBy { it.period.start },
            past = all.filter { it.isPast(today) }.sortedByDescending { it.period.end },
            cancelled = all.filter { it.status == ReservationStatus.CANCELLED },
            rejected = all.filter { it.status == ReservationStatus.REJECTED }
        )
    }
}