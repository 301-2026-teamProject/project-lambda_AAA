package com.example.lambda_aaa.model

/**
 * Type of notification that the user receives
 * WINNER: won the lottery
 * NOT_SELECTED: did not win the lottery
 * RESERVATION_CONFIRMED: booked a campsite after winning a lottery
 * CANCELLATION: cancelled a campsite booking
 * OWNER_BROADCAST: broadcast from the campsite owner
 * OWNER_MESSAGE: message from the campsite owner
 */
enum class NotificationType {
    WINNER,
    NOT_SELECTED,
    RESERVATION_CONFIRMED,
    CANCELLATION,
    OWNER_BROADCAST,
    OWNER_MESSAGE
}
