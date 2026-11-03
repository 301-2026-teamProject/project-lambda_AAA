package com.example.lambda_aaa.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.time.LocalDate

data class DateRange(val start: LocalDate, val end: LocalDate) {
    init {
        require(end.isAfter(start)) { "End date must be after start date" }
    }
}

data class Campsite(
    @DocumentId val cid: String = "",
    var ownerId: String = "",                 // single source of truth for the owner link
    var name: String = "",
    var address: String = "",
    var mapLocation: GeoPoint? = null,
    var description: String = "",             // amenities
    var maxOccupancy: Int = 0,
    var tags: List<String> = emptyList(),
    var imageUrl: String? = null,
    var rulesAndPolicy: String = "",
    var cancellationPolicy: String = "",
    var pricePerNight: Double = 0.0,
    var ratingAverage: Double = 0.0,
    var ratingCount: Int = 0,
    var lottery: Lottery? = null,
    var availablePeriod: DateRange = DateRange(LocalDate.now().minusDays(1), LocalDate.now()),
    @ServerTimestamp var creationDate: Timestamp? = null,
    @ServerTimestamp var lastUpdatedDate: Timestamp? = null
)