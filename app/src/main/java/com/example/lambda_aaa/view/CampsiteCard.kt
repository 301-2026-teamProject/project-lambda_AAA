package com.example.lambda_aaa.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lambda_aaa.R
import com.example.lambda_aaa.model.Campsite

@Composable
fun CampsiteCard(campsite: Campsite, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        color = androidx.compose.ui.graphics.Color.White,
        contentColor = TrailInk,
        border = BorderStroke(1.dp, TrailBorder),
    ) {
        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(campsite.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(campsite.address.ifBlank { stringResource(R.string.location_unavailable) },
                    color = TrailMuted, fontSize = 12.sp)
                Text(occupancyText(campsite), color = TrailMuted, fontSize = 12.sp)
                Text(ratingText(campsite), color = TrailMuted, fontSize = 12.sp)
            }
            Spacer(Modifier.width(10.dp))
            Surface(shape = RoundedCornerShape(13.dp), color = TrailThumb) {
                CampsiteImage(campsite, Modifier.size(width = 68.dp, height = 64.dp))
            }
        }
    }
}

@Composable
internal fun CampsiteImage(campsite: Campsite, modifier: Modifier = Modifier) {
    val fallback = painterResource(R.drawable.campsite_placeholder)
    AsyncImage(
        model = campsite.imageUrl?.takeIf { it.isNotBlank() },
        contentDescription = stringResource(R.string.campsite_photo, campsite.name),
        placeholder = fallback,
        fallback = fallback,
        error = fallback,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    )
}

@Composable
internal fun ratingText(campsite: Campsite): String =
    if (campsite.ratingCount > 0) stringResource(R.string.campsite_rating, campsite.ratingAverage)
    else stringResource(R.string.campsite_unrated)

@Composable
internal fun occupancyText(campsite: Campsite): String =
    if (campsite.maxOccupancy > 0)
        pluralStringResource(R.plurals.campsite_occupancy, campsite.maxOccupancy, campsite.maxOccupancy)
    else stringResource(R.string.occupancy_unavailable)
