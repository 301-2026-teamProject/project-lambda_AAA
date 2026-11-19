package com.example.lambda_aaa.model

/**
 * Potential states for an entrant to be in after joining a lottery
 * WAITING: waiting for selection
 * WITHDRAWN: left the waitlist after joining
 * SELECTED: selected to choose campsite
 * NOT_SELECTED: not selected to choose campsite
 * DECLINED: declined booking after winning lottery
 * BOOKED: booked campsite after winning lottery
 */
enum class EntryStatus {
    WAITING,
    WITHDRAWN,
    SELECTED,
    NOT_SELECTED,
    DECLINED,
    BOOKED
}
