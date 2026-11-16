package com.example.lambda_aaa.model.detail

import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.Lottery
import com.example.lambda_aaa.model.LotteryEntry


interface CampsiteDetailDataSource {
    fun getCampsite(cid: String, result: (Boolean, Campsite?, String?) -> Unit)
    fun getLotteries(campsite: Campsite, result: (Boolean, List<Lottery>, String?) -> Unit)
    fun getEntrantId(result: (Boolean, String?, String?) -> Unit)
    fun getEntry(lotteryId: String, uid: String, result: (Boolean, LotteryEntry?, String?) -> Unit)

    /**
     * Joins the waitlist for a specific lottery
     * Collaborators: EntryRepository, LotteryEntry
     */
    fun join(lotteryId: String, uid: String, result: (Boolean, String?) -> Unit)

    /**
     * Withdraws an entrant from the waitlist for a specific lottery
     * Collaborators: EntryRepository, LotteryEntry
     */
    fun leave(lotteryId: String, uid: String, result: (Boolean, String?) -> Unit)
}
