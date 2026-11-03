package com.example.lambda_aaa.model.detail

import com.example.lambda_aaa.model.*
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.firestore
import com.google.firebase.installations.FirebaseInstallations
import java.time.LocalDate

/** Reads the current Lottery.toMap() schema without changing the team's shared classes. */
class FirestoreCampsiteDetailDataSource(
    private val campsites: CampsiteRepository,
    private val db: FirebaseFirestore = Firebase.firestore,
    private val entries: EntryRepository = EntryRepository(),
    private val installationId: () -> Task<String> = { FirebaseInstallations.getInstance().id },
) : CampsiteDetailDataSource {
    override fun getCampsite(cid: String, result: (Boolean, Campsite?, String?) -> Unit) =
        campsites.getCampsite(cid, result)

    override fun getLotteries(campsite: Campsite, result: (Boolean, List<Lottery>, String?) -> Unit) {
        db.collection("lotteries").whereEqualTo("campsiteID", campsite.cid).get(Source.SERVER)
            .addOnSuccessListener { snapshot ->
                val lotteries = try {
                    snapshot.documents.map { decodeLottery(it.id, it.data.orEmpty(), campsite) }
                } catch (e: RuntimeException) {
                    result(false, emptyList(), e.message)
                    return@addOnSuccessListener
                }
                result(true, lotteries, null)
            }.addOnFailureListener { result(false, emptyList(), it.message) }
    }

    override fun getEntrantId(result: (Boolean, String?, String?) -> Unit) {
        installationId().addOnSuccessListener { deviceId ->
            db.collection("users").whereEqualTo("userDeviceID", deviceId).limit(2).get(Source.SERVER)
                .addOnSuccessListener { snapshot ->
                    // Never join as a placeholder, an installation ID, or an ambiguous duplicate profile.
                    if (snapshot.size() == 1) result(true, snapshot.documents.single().id, null)
                    else result(false, null, "A unique saved profile is required.")
                }.addOnFailureListener { result(false, null, it.message) }
        }.addOnFailureListener { result(false, null, it.message) }
    }

    override fun getEntry(lotteryId: String, uid: String, result: (Boolean, LotteryEntry?, String?) -> Unit) =
        entries.getEntry(lotteryId, uid, result)

    override fun join(lotteryId: String, uid: String, result: (Boolean, String?) -> Unit) {
        // shortcut: current Lottery has no capacity field; wire capacity here when the shared model adds it.
        entries.joinWaitlist(lotteryId, uid, 0, result)
    }

    companion object {
        fun decodeLottery(id: String, fields: Map<String, Any?>, campsite: Campsite): Lottery {
            require(id.isNotBlank() && fields["campsiteID"] == campsite.cid) { "Invalid lottery campsite." }
            fun range(prefix: String): DateRange? {
                val start = fields["${prefix}Start"]
                val end = fields["${prefix}End"]
                if (start == null && end == null) return null
                require(start is String && end is String) { "Incomplete $prefix." }
                return DateRange(LocalDate.parse(start), LocalDate.parse(end))
            }
            val count = fields["numWinners"] as? Number
                ?: throw IllegalArgumentException("Missing winner count.")
            require(count.toLong() in 0..Int.MAX_VALUE.toLong() && count.toDouble() == count.toLong().toDouble())
            return Lottery(campsite, range("bookingPeriod"), range("registrationPeriod"), count.toInt(), id)
        }
    }
}
