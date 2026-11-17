package com.example.lambda_aaa.model

/**
 * One row in the messaging list: a person the current user can message, in the context of a lottery.
 *
 * For an owner this is an entrant who joined one of their lotteries; for an entrant it is the
 * owner of a campsite whose lottery they joined.
 *
 * @property uid user id of the person who will receive the message
 * @property name display name of that person (fallback text if their profile has no name)
 * @property lotteryId lottery that links the two users
 * @property lotteryName name of that lottery
 * @property campsiteName name of the campsite the lottery belongs to
 */
data class MessageContact(
    val uid: String,
    val name: String,
    val lotteryId: String,
    val lotteryName: String,
    val campsiteName: String,
)
