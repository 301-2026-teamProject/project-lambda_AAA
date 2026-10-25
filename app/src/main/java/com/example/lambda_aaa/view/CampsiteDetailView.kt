package com.example.lambda_aaa.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lambda_aaa.model.Campsite
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.lambda_aaa.controller.CampsiteDetailController
import android.R.attr.padding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampsiteDetailView(
    campsiteDetailController: CampsiteDetailController,
    onBack: () -> Unit,
    onEdit: () -> Unit,
) {
    val campsite = campsiteDetailController.campsite
    val errorMessage: String? = campsiteDetailController.errorMessage

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Campsite Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            campsiteDetailController.isLoading -> Box(
                Modifier.fillMaxSize().padding(padding),
                Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            errorMessage != null -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                campsite?.let { campsite ->
                    Text(campsite.name, style = MaterialTheme.typography.headlineMedium)
                    DetailRow("Address", campsite.address)
                    DetailRow("Maximum occupancy", campsite.maxOccupancy.toString())
                    DetailRow(
                        "Rating",
                        if (campsite.ratingCount == 0) "No ratings yet"
                        else "%.1f (%d ratings)".format(
                            campsite.ratingAverage,
                            campsite.ratingCount
                        )
                    )
                    DetailRow("Tags", campsite.tags.joinToString().ifBlank { "None" })
                    DetailRow("Description", campsite.description.ifBlank { "—" })
                    DetailRow("Rules and policy", campsite.rulesAndPolicy.ifBlank { "—" })
                    DetailRow("Cancellation policy", campsite.cancellationPolicy.ifBlank { "—" })
                    campsite.mapLocation?.let {
                        DetailRow("Map location", "%.5f, %.5f".format(it.latitude, it.longitude))
                    }
                }
                Button(onClick = onEdit, modifier = Modifier.fillMaxWidth()) { Text("Edit") }

                /* Clickable to lottery / to add lottery here */
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}