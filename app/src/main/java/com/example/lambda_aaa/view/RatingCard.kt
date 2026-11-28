package com.example.lambda_aaa.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lambda_aaa.model.Rating
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

/**
 * Read-only card for displaying a single campsite rating.
 *
 * Header: reviewer name, star score, and the time the rating was made.
 * Body: the written review (feedback).
 *
 * @param rating the rating to display
 * @param modifier modifier for the card surface
 */
@Composable
fun RatingCard(
    rating: Rating,
    admin: Boolean,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {},
    ) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        color = Color.White,
        contentColor = HeaderColor,
        border = BorderStroke(1.dp, BorderColor),
    ) {
        Column(Modifier.padding(14.dp)) {

            // Header: name + stars on the left, timestamp on the right
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = rating.name.ifBlank { "Anonymous" },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HeaderColor,
                    )
                    StarDisplay(score = rating.stars)
                }
                Text(
                    text = formatRatingTime(rating.creationDate),
                    fontSize = 11.sp,
                    color = SubText,
                )
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = BorderColor, thickness = 1.dp)
            Spacer(Modifier.height(10.dp))

            // Context: the review text
            Text(
                text = rating.feedback.ifBlank { "No written review." },
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = if (rating.feedback.isBlank()) SubText else HeaderColor,
            )

            if (admin) {
                Button(
                    onClick = { onClick(rating.rid) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AddGreen,
                        contentColor = Color.White,
                    )
                ) {
                    Text("Remove Rating")
                }
            }
        }
    }
}

/**
 * Non-interactive version of StarScore from EntrantRatingView.
 * Same icons and color, but no click handling, so the score can't be changed.
 *
 * @param score number of filled stars (0..maxStars)
 * @param starSize size of each star icon
 */
@Composable
fun StarDisplay(score: Int, modifier: Modifier = Modifier, starSize: Dp = 18.dp, maxStars: Int = 5) {
    Row(modifier = modifier.semantics { contentDescription = "Rated $score out of $maxStars stars" }) {
        for (i in 1..maxStars) {
            Icon(
                imageVector = if (score >= i) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = null, // described once on the Row
                modifier = Modifier.size(starSize),
                tint = Color(0xFFFFC107),
            )
        }
    }
}

private fun formatRatingTime(timestamp: Timestamp?): String =
    timestamp?.toDate()?.let {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(it)
    } ?: "Time unavailable"