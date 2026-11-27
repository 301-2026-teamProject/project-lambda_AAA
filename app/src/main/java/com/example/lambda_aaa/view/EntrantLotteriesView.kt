package com.example.lambda_aaa.view

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.example.lambda_aaa.model.EntrantLotteriesViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import com.example.lambda_aaa.model.LotteryItem
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.text.style.TextAlign
import java.time.format.DateTimeFormatter
import com.example.lambda_aaa.model.DateRange

/**
 * Screen for entrants lottery
 */
@Composable
fun EntrantLotteryView(viewModel: EntrantLotteriesViewModel = viewModel()) {
    val state = viewModel.uiState
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val selected = state.lotteries.find { it.lottery.id == selectedId }

    LaunchedEffect(Unit) { viewModel.load() }

    if (selected != null) {
        BackHandler { selectedId = null }
        LotteryDetailsScreen(item = selected, onClose = { selectedId = null })
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Lottery",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.error != null -> Text(state.error, color = MaterialTheme.colorScheme.error)
                state.lotteries.isEmpty() -> Text("No lotteries are open for registration right now.")
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.lotteries, key = { it.lottery.id }) { item ->
                        LotteryCard(
                            item = item,
                            onClick = { selectedId = item.lottery.id }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Displays lottery details when lottery is clicked on
 * @param item stores lottery and its associated campsite names
 */
@Composable
fun LotteryDetailsScreen(item: LotteryItem, onClose: () -> Unit) {
    val lottery = item.lottery

    Column(Modifier.fillMaxSize()) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
            Text(
                text = lottery.name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(48.dp)) // balances the close button so the title stays centred
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DetailRow("Campsites", item.campsiteNames.ifBlank { "Not available" })
            DetailRow("Registration period", lottery.registrationPeriod.display())
            DetailRow("Booking period", lottery.bookingPeriod.display())
            DetailRow("Number of winners", lottery.numWinners.toString())
            DetailRow("Waitlist capacity", lottery.waitlistCapacity.toString())
        }
    }
}

/**
 * Helper function that displays each line in LotteryDetailScreen
 */
@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

private val dateFormat = DateTimeFormatter.ofPattern("MMM d, yyyy")

private fun DateRange?.display(): String =
    this?.let { "${it.start.format(dateFormat)} – ${it.end.format(dateFormat)}" } ?: "Not set"