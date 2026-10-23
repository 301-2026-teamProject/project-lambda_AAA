package com.example.lambda_aaa.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lambda_aaa.controller.CampsiteManagementController
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.CampsiteTags
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew

/**
 * View: only displays state from the controller and forwards the Save click.
 * Usage: CampsiteEditView(cid = "abc123", onDone = { navController.popBackStack() })
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagSelector(
    selected: List<String>,
    onChange: (List<String>) -> Unit,
    options: List<String> = CampsiteTags.ALL,
) {
    Text("Tags", style = MaterialTheme.typography.titleSmall)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // (options + selected).distinct() keeps any old tag that isn't in the list anymore
        (options + selected).distinct().forEach { tag ->
            FilterChip(
                selected = tag in selected,
                onClick = { onChange(if (tag in selected) selected - tag else selected + tag) },
                label = { Text(tag) },
            )
        }
    }
}

@Composable
fun CampsiteEditorView(
    cid: String,
    isNew: Boolean,
    onDone: (String) -> Unit
) {
    val controller = remember(cid, isNew) { CampsiteManagementController(cid, isNew) }

    CampsiteEditorContent(
        isNew = isNew,
        campsite = controller.campsite,
        isLoading = controller.isLoading,
        errorMessage = controller.errorMessage,
        save = { name, address, desc, rule, cancel, tags, maxOcc, onSave ->
            controller.save(name, address, desc, rule, cancel, tags, maxOcc, onSave)
               },
        onSave = onDone,
        onBack = { }  // back button logic
    )
}

@Composable
fun CampsiteEditorContent(
    isNew: Boolean,
    campsite: Campsite?,
    isLoading: Boolean,
    errorMessage: String?,
    save: (String, String, String, String, String, List<String>, String, (String) -> Unit) -> Unit,
    onSave: (String) -> Unit,
    onBack: () -> Unit
) {
    when {
        isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }

        campsite == null -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text(errorMessage ?: "Campsite does not exist.", color = MaterialTheme.colorScheme.error)
        }

        else -> {
            // Text fields start with the campsite's existing values.
            var name by remember(campsite) { mutableStateOf(campsite.name) }
            var address by remember(campsite) { mutableStateOf(campsite.address) }
            var description by remember(campsite) { mutableStateOf(campsite.description) }
            var rules by remember(campsite) { mutableStateOf(campsite.rulesAndPolicy) }
            var cancellation by remember(campsite) { mutableStateOf(campsite.cancellationPolicy) }
            var tags by remember(campsite) { mutableStateOf(campsite.tags.toList()) }
            var maxOccupancy by remember(campsite) { mutableStateOf(campsite.maxOccupancy.toString()) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "History"
                        )
                    }
                }
                Text(if (isNew) "Register Campsite" else "Edit Campsite Information", style = MaterialTheme.typography.headlineSmall)

                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Name of your campsite") }, modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = address, onValueChange = { address = it },
                    label = { Text("Address") }, modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = description, onValueChange = { description = it },
                    label = { Text("Description") }, minLines = 3, modifier = Modifier.fillMaxWidth(),
                )
                TagSelector(selected = tags, onChange = { tags = it })
                OutlinedTextField(
                    value = maxOccupancy,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) maxOccupancy = input   // ignores letters, '-', '.', pasted junk
                    },
                    label = { Text("Maximum occupancy") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = rules, onValueChange = { rules = it },
                    label = { Text("Rules and policy") }, minLines = 3, modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = cancellation, onValueChange = { cancellation = it },
                    label = { Text("Cancellation policy") }, minLines = 2, modifier = Modifier.fillMaxWidth(),
                )
                // Shown but not editable: the average comes from entrant ratings.
                OutlinedTextField(
                    value = campsite.ratingAverage.toString(), onValueChange = {},
                    label = { Text("Rating average") }, readOnly = true, enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                )

                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = { save(name, address, description, rules, cancellation, tags, maxOccupancy, onSave) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Save") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CampsiteEditorViewPreview() {
    CampsiteEditorContent(
        isNew = false,
        campsite = Campsite(name = "University of Alberta"),
        isLoading = false,
        errorMessage = "Something happened?",
        save = { _,_,_,_,_,_,_,_ ->},
        onBack = {},
        onSave = {}
    )
}