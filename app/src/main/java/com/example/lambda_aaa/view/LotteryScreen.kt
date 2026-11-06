package com.example.lambda_aaa.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lambda_aaa.controller.CampsiteDropdown
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.LotteryForm
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import com.example.lambda_aaa.controller.CapacityCheck
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.example.lambda_aaa.model.Lottery
import com.example.lambda_aaa.model.LotteryViewModel

// CreateLotteryScreen is a screen to create lottery associated with a campsite
// DateField is an outlined text field for booking and registration periods with Android's date picker dialog
// NumberField is an outlined text field that ensures inputs are numbers
// toLocalDate and toEpochMilli functions convert to their respective formats
@Composable
fun CreateLotteryScreen(
    campsites: List<Campsite>,
    onClose: () -> Unit,
    viewModel: LotteryViewModel = viewModel()
) {
    var form by remember { mutableStateOf(LotteryForm()) }
    val lottery = form.toLotteryOrNull()
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    errorMessage?.let {
        Text(it, color = MaterialTheme.colorScheme.error)
    }

    Column(Modifier.fillMaxSize()) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
            Text(
                "Create lottery",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(48.dp)) // balances the close button so the title stays centred
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val selectedCampsite = campsites.find { it.cid == form.campsiteID }
            CampsiteDropdown(campsites, selectedCampsite) { picked ->
                form = form.copy(campsiteID = picked.cid)
            }
            OutlinedTextField(
                value = form.name,
                onValueChange = { form = form.copy(name = it) },
                label = { Text("Lottery name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DateField("Registration opens", form.regStart, Modifier.weight(1f)) {
                    form = form.copy(regStart = it)
                }
                DateField("Registration closes", form.regEnd, Modifier.weight(1f)) {
                    form = form.copy(regEnd = it)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DateField("Booking starts", form.bookStart, Modifier.weight(1f)) {
                    form = form.copy(bookStart = it)
                }
                DateField("Booking ends", form.bookEnd, Modifier.weight(1f)) {
                    form = form.copy(bookEnd = it)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberField("Number of winners", form.winnersText, Modifier.weight(1f).testTag("winnersField")) {
                    form = form.copy(winnersText = it)
                }
                NumberField("Waitlist capacity", form.capacityText, Modifier.weight(1f).testTag("capacityField")) {
                    form = form.copy(capacityText = it)
                }
            }

            CapacityCheck(form.numWinners, form.waitlistCapacity)
            Button(
                enabled = lottery != null && !isSaving,
                onClick = {
                    lottery?.let {
                        isSaving = true
                        errorMessage = null
                        viewModel.createLottery(it) { success, error ->
                            isSaving = false
                            if (success) onClose()
                            else errorMessage = error ?: "Couldn't create lottery"
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D5A40)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("createLotteryButton")
            ) { Text(if (isSaving) "Creating" else "Create lottery") }
        }
    }
}

@Composable
fun EditLotteryScreen(
    lottery: Lottery,
    onClose: () -> Unit,
    viewModel: LotteryViewModel = viewModel()
) {
    var form by remember { mutableStateOf(LotteryForm(campsiteID = lottery.campsiteID, name = lottery.name, lottery.registrationPeriod!!.start, lottery.registrationPeriod!!.end, lottery.bookingPeriod!!.start, lottery.bookingPeriod!!.end, lottery.numWinners.toString(), lottery.waitlistCapacity.toString())) }
    val updatedLottery = form.toLotteryOrNull()
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    errorMessage?.let {
        Text(it, color = MaterialTheme.colorScheme.error)
    }

    Column(Modifier.fillMaxSize()) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
            Text(
                "Edit lottery",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(48.dp)) // balances the close button so the title stays centred
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = form.name,
                onValueChange = { form = form.copy(name = it) },
                label = { Text("Lottery name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DateField("Registration opens", form.regStart, Modifier.weight(1f)) {
                    form = form.copy(regStart = it)
                }
                DateField("Registration closes", form.regEnd, Modifier.weight(1f)) {
                    form = form.copy(regEnd = it)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DateField("Booking starts", form.bookStart, Modifier.weight(1f)) {
                    form = form.copy(bookStart = it)
                }
                DateField("Booking ends", form.bookEnd, Modifier.weight(1f)) {
                    form = form.copy(bookEnd = it)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberField("Number of winners", form.winnersText, Modifier.weight(1f).testTag("winnersField")) {
                    form = form.copy(winnersText = it)
                }
                NumberField("Waitlist capacity", form.capacityText, Modifier.weight(1f).testTag("capacityField")) {
                    form = form.copy(capacityText = it)
                }
            }

            CapacityCheck(form.numWinners, form.waitlistCapacity)
            Button(
                enabled = !isSaving,
                onClick = {
                    updatedLottery?.let {
                        isSaving = true
                        errorMessage = null
                        viewModel.editLottery(it, lotteryID = lottery.id) { success, error ->
                            isSaving = false
                            if (success) onClose()
                            else errorMessage = error ?: "Couldn't edit lottery"
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D5A40)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("createLotteryButton")
            ) { Text(if (isSaving) "Saving" else "Update lottery") }
        }
    }
}

@Composable
fun DateField(
    label: String,
    value: LocalDate?,
    modifier: Modifier = Modifier,
    onValueChange: (LocalDate) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    Box(modifier) {
        OutlinedTextField(
            value = value?.toString() ?: "",
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        // Transparent layer on top, because a read-only text field swallows clicks
        Box(
            Modifier
                .matchParentSize()
                .clickable { showPicker = true })
    }
    if (showPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = value?.toEpochMillis())
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    enabled = state.selectedDateMillis != null,
                    onClick = {
                        onValueChange(state.selectedDateMillis!!.toLocalDate())
                        showPicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

@Composable
fun NumberField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.all(Char::isDigit) && it.length <= 5) onValueChange(it) },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
    )
}

// The picker returns UTC milliseconds, so convert using UTC

fun LocalDate.toEpochMillis(): Long =
    atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()