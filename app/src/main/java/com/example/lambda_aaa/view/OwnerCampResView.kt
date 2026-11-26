package com.example.lambda_aaa.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.format.DateTimeFormatter
import com.example.lambda_aaa.model.OwnerCampsiteReservationsViewModel
import com.example.lambda_aaa.ui.theme.ForestGreen
import com.example.lambda_aaa.model.ReservationView
import com.example.lambda_aaa.model.ReservationItem
import com.example.lambda_aaa.model.Reservation
import com.example.lambda_aaa.model.ReservationStatus
import com.example.lambda_aaa.ui.theme.LightGreen
import java.lang.System.console

@Composable
fun OwnerCampsiteReservationsView(
    ownerId: String,
    campsiteId: String,
    onBack: () -> Unit,
    viewModel: OwnerCampsiteReservationsViewModel = viewModel()
) {
    val state = viewModel.uiState

    LaunchedEffect(ownerId, campsiteId) { viewModel.load(ownerId, campsiteId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Reservations",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            ViewSelector(selected = state.selectedView, onSelect = viewModel::selectView)
        }

        Spacer(Modifier.height(16.dp))

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ForestGreen)
            }
            state.error != null -> {
                Text(state.error, color = MaterialTheme.colorScheme.error)
                println("here is my print statement " + state.error)
            }
            state.visible.isEmpty() -> Text(
                "No ${state.selectedView.label.lowercase()} reservations.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.visible, key = { it.reservation.id }) { item ->
                    ReservationCard(item)
                }
            }
        }
    }
}

private val SelectorBackground = Color(0xFFE1ECE2)  // darker, greener version of 0xFFF8FAF7

@Composable
private fun ViewSelector(
    selected: ReservationView,
    onSelect: (ReservationView) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(SelectorBackground)
                .clickable { expanded = true }
                .padding(start = 12.dp, end = 4.dp, top = 6.dp, bottom = 6.dp)
        ) {
            Text(
                selected.label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = ForestGreen
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = ForestGreen)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ReservationView.entries.forEach { view ->
                DropdownMenuItem(
                    text = { Text(view.label) },
                    onClick = {
                        onSelect(view)
                        expanded = false
                    }
                )
            }
        }
    }
}

private val reservationDateFormat = DateTimeFormatter.ofPattern("MMM d, yyyy")

@Composable
fun ReservationCard(item: ReservationItem) {
    val reservation = item.reservation

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, LightGreen.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(16.dp)) {
            StatusBadge(reservation)

            Spacer(Modifier.height(8.dp))

            Text(
                text = item.entrantName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "${reservation.period.start.format(reservationDateFormat)} – " +
                        reservation.period.end.format(reservationDateFormat),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatusBadge(reservation: Reservation) {
    val (label, color) = when {
        reservation.status == ReservationStatus.REJECTED -> "Rejected" to Color(0xFFB3261E)
        reservation.status == ReservationStatus.CANCELLED -> "Cancelled" to Color(0xFF1E3A5F)
        reservation.isPast() -> "Past" to Color(0xFF6B7280)
        else -> "Active" to ForestGreen
    }

    Text(
        text = label,
        color = Color.White,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .background(color, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}