package com.example.lambda_aaa.model

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore

/**
 * Repository of reservations
 * @property db connection to Firestore
 * @property reservationRef Firestore's reservations collection
 */
class ReservationRepository(db: FirebaseFirestore = Firebase.firestore) {

    private val reservationRef = db.collection("reservations")

    /** Availability for a campsite in this lottery; winner authorization belongs to the booking flow. */
    fun getAvailableDates(
        campsite: Campsite,
        lottery: Lottery,
        result: (Boolean, List<DateRange>, String?) -> Unit
    ) {
        if (campsite.cid.isBlank() || campsite.cid !in lottery.campsiteIDs) {
            result(false, emptyList(), "This campsite is not part of the lottery.")
            return
        }
        if (lottery.bookingPeriod == null) {
            result(false, emptyList(), "The lottery has no booking period.")
            return
        }
        getBookedDates(campsite.cid) { ok, booked, message ->
            if (!ok) {
                result(false, emptyList(), message)
            } else {
                result(true, availableReservationPeriods(
                    campsite.availablePeriod, lottery.bookingPeriod, booked
                ), null)
            }
        }
    }

    fun create(reservation: Reservation, onResult: (Boolean, String?) -> Unit) {
        // Refuse if an active reservation already overlaps these dates
        getBookedDates(reservation.campsiteId) { ok, booked, message ->
            if (!ok) {
                onResult(false, message)
                return@getBookedDates
            }
            if (booked.any { it.overlaps(reservation.period) }) {
                onResult(false, "Those dates are already booked.")
                return@getBookedDates
            }
            reservationRef.document(reservation.id).set(reservation.toMap())
                .addOnSuccessListener { onResult(true, null) }
                .addOnFailureListener { onResult(false, it.message) }
        }
    }

    // Booked dates for one campsite (active reservations only)
    fun getBookedDates(campsiteId: String, result: (Boolean, List<DateRange>, String?) -> Unit) {
        reservationRef
            .whereEqualTo("campsiteID", campsiteId)
            .whereEqualTo("status", ReservationStatus.ACTIVE.name)
            .get()
            .addOnSuccessListener { snapshot ->
                result(true, snapshot.decode().map { it.period }, null)
            }
            .addOnFailureListener { result(false, emptyList(), it.message) }
    }

    // All reservations for an owner's campsites, grouped
    fun getOwnerReservations(ownerId: String, result: (Boolean, OwnerReservations?, String?) -> Unit) {
        reservationRef
            .whereEqualTo("ownerID", ownerId)
            .get()
            .addOnSuccessListener { snapshot ->
                result(true, OwnerReservations.from(snapshot.decode()), null)
            }
            .addOnFailureListener { result(false, null, it.message) }
    }

    fun reject(reservationId: String, onResult: (Boolean, String?) -> Unit) =
        updateStatus(reservationId, ReservationStatus.REJECTED, onResult)

    fun cancel(reservationId: String, onResult: (Boolean, String?) -> Unit) =
        updateStatus(reservationId, ReservationStatus.CANCELLED, onResult)

    private fun updateStatus(
        reservationId: String,
        status: ReservationStatus,
        onResult: (Boolean, String?) -> Unit
    ) {
        reservationRef.document(reservationId)
            .update("status", status.name)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    // Skip malformed documents instead of crashing (same lesson as the lotteries)
    private fun QuerySnapshot.decode(): List<Reservation> =
        documents.mapNotNull { doc ->
            runCatching { Reservation.fromMap(doc.id, doc.data.orEmpty()) }.getOrNull()
        }

    fun getCampsiteReservations(
        ownerId: String,
        campsiteId: String,
        result: (Boolean, OwnerReservations?, String?) -> Unit
    ) {
        reservationRef
            .whereEqualTo("ownerID", ownerId)
            .whereEqualTo("campsiteID", campsiteId)
            .get()
            .addOnSuccessListener { snapshot ->
                result(true, OwnerReservations.from(snapshot.decode()), null)
            }
            .addOnFailureListener { result(false, null, it.message) }
    }
}
