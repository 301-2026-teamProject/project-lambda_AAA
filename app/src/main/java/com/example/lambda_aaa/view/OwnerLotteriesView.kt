package com.example.lambda_aaa.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lambda_aaa.model.OwnerLotteriesViewModel
import com.example.lambda_aaa.model.LotteryItem
import androidx.compose.material.icons.filled.Edit

/**
 * Lottery screen for owners
 * @param ownerId string of owner's id
 * @param onOpenWaitlist calls LotteryWaitlistView
 * @param viewModel holds logic and state for the view
 */
@Composable
fun OwnerLotteryView(
    ownerId: String,
    onOpenWaitlist: (String) -> Unit,
    viewModel: OwnerLotteriesViewModel = viewModel()
) {
    val state = viewModel.uiState
    var showCreate by rememberSaveable { mutableStateOf(false) }
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    val editingLottery = state.lotteries.find { it.lottery.id == editingId }?.lottery

    LaunchedEffect(ownerId) { viewModel.load(ownerId) }

    when {
        showCreate -> {
            BackHandler { showCreate = false }
            CreateLotteryScreen(
                ownerId = ownerId,
                campsites = state.ownedCampsites,
                onClose = {
                    showCreate = false
                    viewModel.load(ownerId)
                }
            )
        }

        editingLottery != null -> {
            BackHandler { editingId = null }
            EditLotteryScreen(
                lottery = editingLottery,
                onClose = {
                    editingId = null
                    viewModel.load(ownerId)  // refresh so edits show up
                }
            )
        }

        else -> Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lotteries",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { showCreate = true },
                    enabled = !state.isLoading
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create lottery")
                }
            }

            Spacer(Modifier.height(16.dp))

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                state.error != null -> Text(state.error, color = MaterialTheme.colorScheme.error)
                state.lotteries.isEmpty() -> Text("No lotteries yet. Tap + to create one.")
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.lotteries, key = { it.lottery.id }) { item ->
                        LotteryCard(
                            item = item,
                            onClick = { onOpenWaitlist(item.lottery.id) },
                            onEdit = { editingId = item.lottery.id }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Stores each of the owner's lottery as a card
 * @param item lottery object with its associated campsites
 * @param onClick goes to waitlist view
 * @param onEdit goes to edit lottery screen
 */
@Composable
fun LotteryCard(
    item: LotteryItem,
    onClick: () -> Unit,
    onEdit: (() -> Unit)? = null
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f).padding(vertical = 8.dp)) {
                Text(item.lottery.name, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    item.campsiteNames,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (onEdit != null) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit lottery")
                }
            }
        }
    }
}