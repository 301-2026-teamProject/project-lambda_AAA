package com.example.lambda_aaa.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.controller.CampsiteOwnerController
import androidx.compose.runtime.remember

@Composable
fun CampsiteOwnerView(
    uid: String,
    controller: CampsiteOwnerController,
    onClickRegister: () -> Unit,  // clicking register lead user to register page
    onClickCampsite: (String) -> Unit,  // clicking an owned campsite lead user to edit page
    editMessage: Pair<Boolean, String>?,
    onEditMessageShown: () -> Unit
) {
    LaunchedEffect(Unit) {
        controller.refresh()
    }
    CampsiteOwnerContent(
        campsiteEditMessage = editMessage,
        onMessageShown = onEditMessageShown,
        isLoading = controller.isLoading,
        campsites = controller.campsites,
        errorMessage = controller.errorMessage,
        onClickRegister = onClickRegister,
        onClickCampsite = onClickCampsite
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampsiteOwnerContent(
    campsiteEditMessage: Pair<Boolean, String>?,
    onMessageShown: () -> Unit,
    isLoading: Boolean,
    campsites: List<Campsite>,
    errorMessage: String?,
    onClickRegister: () -> Unit,
    onClickCampsite: (String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(campsiteEditMessage) {
        campsiteEditMessage?.let {
            snackbarHostState.showSnackbar(it.second)
            onMessageShown()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Owner Dashboard") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onClickRegister) {
                Icon(Icons.Default.Add, contentDescription = "Register campsite")
            }
        }
    ) { padding ->
        when {
            isLoading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator()
            }
            errorMessage != null -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("My Campsites", style = MaterialTheme.typography.headlineMedium)
                    }
                }
                if (campsites.isEmpty()) {
                    item { Text("You haven't registered any campsites yet. Tap + to add one.") }
                }
                items(campsites, key = { it.cid }) { campsite ->
                    CampsiteCard(campsite) { onClickCampsite(campsite.cid) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CampsiteCard(campsite: Campsite, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(campsite.name, style = MaterialTheme.typography.titleMedium)
            Text(campsite.address, style = MaterialTheme.typography.bodyMedium)
            Text(
                "Max occupancy: ${campsite.maxOccupancy}  •  " +
                        if (campsite.ratingCount == 0) "No ratings yet"
                        else "★ ${"%.1f".format(campsite.ratingAverage)} (${campsite.ratingCount})",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}