package com.example.lambda_aaa.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lambda_aaa.model.OwnerCampsitesViewModel
import com.example.lambda_aaa.ui.theme.ForestGreen
import com.example.lambda_aaa.ui.theme.LightGreen
import com.example.lambda_aaa.ui.theme.DarkForestGreen
import com.example.lambda_aaa.model.Campsite

/**
 * Screen for owner to view their campsites
 * @param ownerId owner id string
 * @param onOpenCampsite handles logic when campsite card is clicked
 * @param viewModel holds logic and data for the view
 */
@Composable
fun OwnerCampsiteView(
    ownerId: String,
    onOpenCampsite: (String) -> Unit,
    viewModel: OwnerCampsitesViewModel = viewModel()
) {
    val state = viewModel.uiState

    LaunchedEffect(ownerId) { viewModel.load(ownerId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Campsites",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(16.dp))

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ForestGreen)
            }
            state.error != null -> Text(state.error, color = MaterialTheme.colorScheme.error)
            state.campsites.isEmpty() -> Text(
                "You don't have any campsites yet.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.campsites, key = { it.cid }) { campsite ->
                    CampsiteViewCard(campsite, onClick = { onOpenCampsite(campsite.cid) })
                }
            }
        }
    }
}

/**
 * Displays each campsite as a clickable card
 * @param campsite campsite object
 * @param onClick handles logic when the card is clicked
 */
@Composable
fun CampsiteViewCard(campsite: Campsite, onClick: () -> Unit) {
    Card(
        onClick = onClick ,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, LightGreen.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = campsite.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                RatingBadge(campsite.ratingAverage)
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = campsite.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Displays campsite's rating in the card
 * @param rating campsite's rating
 */
@Composable
private fun RatingBadge(rating: Double) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(LightGreen.copy(alpha = 0.4f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Icon(
            Icons.Default.Star,
            contentDescription = null,
            tint = DarkForestGreen,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = if (rating > 0) "%.2f".format(rating) else "New",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = DarkForestGreen
        )
    }
}