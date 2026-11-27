package com.example.lambda_aaa.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Holds information about a user rating.
 *
 * Stores data about the number of stars and feedback a user gave for a campsite.
 *
 * @property rid the unique rating ID for the rating
 * @property stars the number of stars the user gave the campsite
 * @property feedback the feedback the user wrote for the campsite
 * @property creationDate the time when the rating was made
 */
class Rating(
    @DocumentId val rid: String = "",
    var stars: Int = 1,
    var feedback: String = "",
    @ServerTimestamp val creationDate: Timestamp? = null
)