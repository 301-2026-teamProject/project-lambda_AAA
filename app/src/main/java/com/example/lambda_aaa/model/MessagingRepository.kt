package com.example.lambda_aaa.model

/**
 * Works out who a user can message by following the links
 *   campsite.ownerId  <-  lottery.campsiteID  <-  lottery_entries.lotteryId / userId
 *
 * It only composes the existing repositories; it does not write anything. Pass in the shared
 * CampsiteRepository / LotteryRepository instances so extra snapshot listeners are not created.
 *
 * @property campsites source of campsites and their owners
 * @property lotteries source of lotteries and their campsite ids
 * @property entries source of waitlist entries (lotteryId + userId)
 * @property users source of display names
 */
class MessagingRepository(
    private val campsites: CampsiteRepository,
    private val lotteries: LotteryRepository,
    private val entries: EntryRepository,
    private val users: UserRepository,
) {
    fun allowContact(entryStatus: EntryStatus): Boolean = when (entryStatus) {
        EntryStatus.WITHDRAWN -> return false
        EntryStatus.NOT_SELECTED -> return false
        EntryStatus.DECLINED -> return false
        else -> return true
    }

    /** Display name of one user, or null if unknown. */
    fun displayName(uid: String, onResult: (String?) -> Unit) {
        users.getNames(setOf(uid)) { onResult(it[uid]) }
    }

    /**
     * Entrants (one card per entrant per lottery) who joined any lottery on a campsite owned by
     * ownerId.
     * Withdrawn entries are excluded.
     */
    fun ownerContacts(
        ownerId: String,
        onResult: (Boolean, List<MessageContact>, String?) -> Unit
    ) {
        campsites.getOwnerCampsites(ownerId) { ok, owned, msg ->
            if (!ok) {
                onResult(false, emptyList(), msg)
                return@getOwnerCampsites
            }
            val campsiteById = owned.associateBy { it.cid }  // map from cid to campsite
            lotteries.getAllLotteries { lotteriesOk, all, lotteriesMsg ->
                if (!lotteriesOk) {
                    onResult(false, emptyList(), lotteriesMsg)
                    return@getAllLotteries
                }
                // filter out unrelated lotteries
                val mine = all.filter { it.campsiteIDs.any { campsiteID -> campsiteID in campsiteById } }
                fanOut<Lottery, Pair<Lottery, List<LotteryEntry>>>(
                    mine,
                    { lottery, done ->
                        entries.getEntriesLottery(lottery.id) { _, list, _ -> done(lottery to list) }
                    }
                ) { results ->
                    // obtain user and lottery pairs
                    val pairs = results.flatMap { (lottery, list) ->
                        // filter out withdrawn entrants
                        list.filter { allowContact(it.status) && it.userId.isNotBlank() }
                            .map { lottery to it.userId }
                    }
                    users.getNames(pairs.map { it.second }.toSet()) { names ->
                        val contacts = pairs
                            .map { (lottery, uid) ->
                                MessageContact(
                                    uid = uid,
                                    name = names[uid] ?: "Unnamed entrant",
                                    lotteryId = lottery.id,
                                    lotteryName = lottery.name,
                                    campsiteName = lottery.campsiteIDs
                                        .mapNotNull { campsiteById[it]?.name }
                                        .joinToString(", "),
                                    // maps the lottery's campsites to the ones in campsiteById's name and joins them
                                )
                            }
                            .distinctBy { it.uid to it.lotteryId }
                            .sortedBy { it.name.lowercase() }
                        onResult(true, contacts, null)
                    }
                }
            }
        }
    }

    /**
     * Owners (one card per lottery) of every lottery the entrant has joined.
     * Withdrawn entries are excluded.
     */
    fun entrantContacts(
        entrantId: String,
        onResult: (Boolean, List<MessageContact>, String?) -> Unit
    ) {
        entries.getEntriesEntrant(entrantId) { _, list, _ ->
            val lotteryIds = list
                .filter { allowContact(it.status) && it.lotteryId.isNotBlank() }
                .map { it.lotteryId }
                .distinct()
            fanOut<String, Lottery?>(
                lotteryIds,
                { id, done -> lotteries.getLottery(id) { _, lottery, _ -> done(lottery) } }
            ) { found ->
                val joined = found.filterNotNull()
                fanOut<String, Campsite?>(
                    joined.flatMap { it.campsiteIDs }.distinct(), //flatMap prevents list of list of campsite ids
                    { cid, done -> campsites.getCampsite(cid) { _, campsite, _ -> done(campsite) } }
                ) { loaded ->
                    val campsiteById = loaded.filterNotNull().associateBy { it.cid }
                    val ownerIds = campsiteById.values.map { it.ownerId }.filter { it.isNotBlank() }.toSet()
                    users.getNames(ownerIds) { names ->
                        val contacts = joined.mapNotNull { lottery ->
                            val campsite = campsiteById[lottery.campsiteIDs[0]] ?: return@mapNotNull null
                            // TEMPORARY FIX SINCE campsiteIDs IS AN ARRAY NOW
                            if (campsite.ownerId.isBlank()) return@mapNotNull null
                            MessageContact(
                                uid = campsite.ownerId,
                                name = names[campsite.ownerId] ?: "Campsite owner",
                                lotteryId = lottery.id,
                                lotteryName = lottery.name,
                                campsiteName = campsite.name,
                            )
                        }.sortedBy { it.name.lowercase() }
                        onResult(true, contacts, null)
                    }
                }
            }
        }
    }

    /**
     * Runs an async call for every item and calls onDone once, after all of them finished,
     * with the results in the same order as items. Firebase callbacks arrive on the main thread,
     * so the plain counter is safe.
     */
    private fun <T, R> fanOut(
        items: List<T>,
        start: (T, (R) -> Unit) -> Unit,
        onDone: (List<R>) -> Unit
    ) {
        if (items.isEmpty()) {
            onDone(emptyList())
            return
        }
        val results = arrayOfNulls<Any?>(items.size)
        var remaining = items.size
        items.forEachIndexed { index, item ->
            start(item) { result ->
                results[index] = result
                remaining--
                if (remaining == 0) {
                    @Suppress("UNCHECKED_CAST")
                    onDone(results.toList() as List<R>)
                }
            }
        }
    }
}
