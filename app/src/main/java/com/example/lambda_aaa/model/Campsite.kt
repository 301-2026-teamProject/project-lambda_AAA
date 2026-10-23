package com.example.lambda_aaa.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp

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
    var ratingAverage: Double = 0.0,
    var ratingCount: Int = 0,
    @ServerTimestamp var creationDate: Timestamp? = null,
    @ServerTimestamp var lastUpdatedDate: Timestamp? = null
)