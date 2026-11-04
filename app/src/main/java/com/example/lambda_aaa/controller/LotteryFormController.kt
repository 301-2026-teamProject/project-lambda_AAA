package com.example.lambda_aaa.controller

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.lambda_aaa.model.Campsite
import kotlin.collections.forEach
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.padding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampsiteDropdown(
    campsites: List<Campsite>,
    selected: Campsite?,
    onSelect: (Campsite) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Campsite") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            campsites.forEach { campsite ->
                DropdownMenuItem(
                    text = { Text(campsite.name) },
                    onClick = {
                        onSelect(campsite)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CapacityCheck(numWinners: Int?, waitlistCapacity: Int?) {
    if (numWinners == null || waitlistCapacity == null) return  // hidden until both are filled in

    val ok = numWinners < waitlistCapacity
    Text(
        text = "Capacity check: $numWinners winner(s) from a maximum of $waitlistCapacity entrant(s)",
        color = if (ok) Color(0xFF1E4620) else Color(0xFF7A1F1F),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (ok) Color(0xFFDDE8DC) else Color(0xFFF6DADA),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    )
}