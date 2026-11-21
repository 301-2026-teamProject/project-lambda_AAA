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
import com.example.lambda_aaa.model.UserProfile

@Composable
fun CampsiteCard(campsite: Campsite, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        color = androidx.compose.ui.graphics.Color.White,
        contentColor = HeaderColor,
        border = BorderStroke(1.dp, BorderColor),
    ) {
        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(campsite.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(campsite.address.ifBlank { stringResource(R.string.location_unavailable) },
                    color = SubText, fontSize = 12.sp)
                Text(occupancyText(campsite), color = SubText, fontSize = 12.sp)
                Text(ratingText(campsite), color = SubText, fontSize = 12.sp)
            }
            Spacer(Modifier.width(10.dp))
            Surface(shape = RoundedCornerShape(13.dp), color = Background) {
                CampsiteImage(campsite, Modifier.size(width = 68.dp, height = 64.dp))
            }
        }
    }
}

@Composable
fun AdminCampsiteCard(campsite: Campsite, ownerName: String?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        color = androidx.compose.ui.graphics.Color.White,
        contentColor = HeaderColor,
        border = BorderStroke(1.dp, BorderColor),
    ) {
        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(campsite.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                if (ownerName != null) {
                    Text("Owner: $ownerName", color = SubText, fontSize = 12.sp)
                } else {
                    Text("Owner: Unknown", color = SubText, fontSize = 12.sp)
                }
                Text(campsite.address.ifBlank { stringResource(R.string.location_unavailable) },
                    color = SubText, fontSize = 12.sp)
                Text("Published Reports: (placeholder)", color = SubText, fontSize = 12.sp)
            }
            Spacer(Modifier.width(10.dp))
            Surface(shape = RoundedCornerShape(13.dp), color = Background) {
                CampsiteImage(campsite, Modifier.size(width = 68.dp, height = 64.dp))
            }
        }
    }
}

@Composable
fun AdminProfileCard(profile: UserProfile, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        color = androidx.compose.ui.graphics.Color.White,
        contentColor = HeaderColor,
        border = BorderStroke(1.dp, BorderColor),
    ) {
        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(profile.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text("Email: ${profile.email}", color = SubText, fontSize = 12.sp)
                Text("Phone: ${profile.phone}", color = SubText, fontSize = 12.sp)
                Text(text = profile.roles.joinToString(separator = ", "), color = SubText, fontSize = 12.sp)
            }
            Spacer(Modifier.width(10.dp))
            Surface(shape = RoundedCornerShape(13.dp), color = Background) {
                ProfileImage(profile, Modifier.size(width = 68.dp, height = 64.dp))
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

@Composable
internal fun ProfileImage(profile: UserProfile, modifier: Modifier = Modifier) {
    val fallback = painterResource(R.drawable.campsite_placeholder)
    AsyncImage(
        model = profile.profilePhotoUrl.takeIf { it.isNotBlank() },
        contentDescription = stringResource(R.string.campsite_photo, profile.name),
        placeholder = fallback,
        fallback = fallback,
        error = fallback,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    )
}