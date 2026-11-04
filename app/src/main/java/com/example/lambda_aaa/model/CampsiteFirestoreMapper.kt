package com.example.lambda_aaa.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.time.LocalDate

/** Keeps LocalDate and circular campsite/lottery objects out of Firestore serialization. */
internal object CampsiteFirestoreMapper {
    fun encode(c: Campsite): Map<String, Any?> = mapOf(
        "ownerId" to c.ownerId, "name" to c.name, "address" to c.address,
        "mapLocation" to c.mapLocation, "description" to c.description,
        "maxOccupancy" to c.maxOccupancy, "tags" to c.tags, "imageUrl" to c.imageUrl,
        "rulesAndPolicy" to c.rulesAndPolicy, "cancellationPolicy" to c.cancellationPolicy,
        "pricePerNight" to c.pricePerNight, "ratingAverage" to c.ratingAverage,
        "ratingCount" to c.ratingCount,
        "availablePeriod" to mapOf("start" to c.availablePeriod.start.toString(), "end" to c.availablePeriod.end.toString()),
        "lottery" to c.lottery?.let {
            require(it.campsite.cid == c.cid) { "Lottery belongs to a different campsite." }
            it.toMap() + ("id" to it.id)
        },
        "creationDate" to c.creationDate, "lastUpdatedDate" to c.lastUpdatedDate,
    )

    fun decode(id: String, fields: Map<String, Any?>): Campsite {
        val c = Campsite(
            cid = id,
            ownerId = fields["ownerId"] as? String ?: "",
            name = fields["name"] as? String ?: "",
            address = fields["address"] as? String ?: "",
            mapLocation = fields["mapLocation"] as? GeoPoint,
            description = fields["description"] as? String ?: "",
            maxOccupancy = (fields["maxOccupancy"] as? Number)?.toInt() ?: 0,
            tags = (fields["tags"] as? List<*>)?.map { it as String }.orEmpty(),
            imageUrl = fields["imageUrl"] as? String,
            rulesAndPolicy = fields["rulesAndPolicy"] as? String ?: "",
            cancellationPolicy = fields["cancellationPolicy"] as? String ?: "",
            pricePerNight = (fields["pricePerNight"] as? Number)?.toDouble() ?: 0.0,
            ratingAverage = (fields["ratingAverage"] as? Number)?.toDouble() ?: 0.0,
            ratingCount = (fields["ratingCount"] as? Number)?.toInt() ?: 0,
            creationDate = fields["creationDate"] as? Timestamp,
            lastUpdatedDate = fields["lastUpdatedDate"] as? Timestamp,
        )
        // Older documents have no availability; retain the shared model's default in that case.
        fields["availablePeriod"]?.let { c.availablePeriod = decodeRange(it) }
        fields["lottery"]?.let { raw ->
            require(raw is Map<*, *>) { "Invalid attached lottery." }
            val lotteryId = raw["id"] as? String
            require(!lotteryId.isNullOrBlank()) { "Attached lottery has no ID." }
            val campsiteId = raw["campsiteID"] ?: (raw["campsite"] as? Map<*, *>)?.get("cid")
            require(campsiteId == null || campsiteId == id) { "Lottery belongs to a different campsite." }
            fun period(name: String): DateRange? {
                raw[name]?.let { return decodeRange(it) }
                val start = raw["${name}Start"]
                val end = raw["${name}End"]
                if (start == null && end == null) return null
                return DateRange(decodeDate(start), decodeDate(end))
            }
            val winners = raw["numWinners"] as? Number
            require(winners != null && winners.toLong() in 0..Int.MAX_VALUE.toLong()
                && winners.toDouble() == winners.toLong().toDouble()) { "Invalid winner count." }
            c.lottery = Lottery(c, period("bookingPeriod"), period("registrationPeriod"), winners.toInt(), lotteryId)
        }
        return c
    }

    private fun decodeRange(raw: Any): DateRange {
        require(raw is Map<*, *>) { "Invalid date range." }
        return DateRange(decodeDate(raw["start"]), decodeDate(raw["end"]))
    }

    private fun decodeDate(raw: Any?): LocalDate = when (raw) {
        is String -> LocalDate.parse(raw)
        // Compatibility with documents written by Firestore's previous automatic LocalDate serializer.
        is Map<*, *> -> LocalDate.of(
            (raw["year"] as Number).toInt(),
            (raw["monthValue"] as Number).toInt(),
            (raw["dayOfMonth"] as Number).toInt(),
        )
        else -> throw IllegalArgumentException("Missing or invalid date.")
    }
}
