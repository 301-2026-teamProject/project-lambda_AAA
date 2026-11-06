package com.example.lambda_aaa.helper

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.lambda_aaa.view.TrailInk
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Allows the user to select a date from a Calendar popup
 *
 * This chosen date will be entered into a Textbox and will be used as a LocalDate
 *
 * @param label an inputted string used on an empty textbox
 * @param value used to edit an outside LocalData function (via by remember)
 * @param onError used to make the textbox red (to show an error) based on called-specified circumstances
 * @param onValueChange callback to change something when the textbox is altered
 * @return String
 */
@Composable
fun DateField(
    label: String,
    value: LocalDate?,
    modifier: Modifier = Modifier,
    onError: Boolean = false,
    onValueChange: (LocalDate) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    fun LocalDate.toEpochMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    fun Long.toLocalDate(): LocalDate =  Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

    Box(modifier) { // The textbox that holds the date when input
        OutlinedTextField(
            value = value?.toString() ?: "",
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            modifier = modifier.fillMaxWidth(),
            isError = onError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
            textStyle = TextStyle(fontSize = 12.sp, color = TrailInk),
        )
        // Transparent layer on top, because a read-only text field swallows clicks
        Box(
            Modifier
                .matchParentSize()
                .clickable { showPicker = true })
    }
    if (showPicker) { // I.e. the calendar that the user can choose from
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

/**
 * Turns a LocalDate value into a readable text
 *
 * A date of the format YYYY-MM-DD will turn into a value following the format "Month day-th, Year"
 *
 * @param start an inputted Date that wil be used to format a string
 * @param end an inputted Date that wil be used to format a string
 * @return String
 */
fun formatDateRangeLabel(start: LocalDate, end: LocalDate): String {
    // Both of these function try to parse their respective dates, otherwise they just return the YYYY-MM-DD of both together

    val monthDayFormat = DateTimeFormatter.ofPattern("MMM d", Locale.US) // Formats dates such as 'Mar 7th'
    val monthFormat =  DateTimeFormatter.ofPattern("MMM", Locale.US) // Format for a four digit year
    val startMonth = start.format(monthFormat) // Abbreviated form of a start month
    val endMonth = end.format(monthFormat) // Same but for the end day
    val startDay = start.dayOfMonth // Start day
    val endDay =  end.dayOfMonth // End day
    val startYear = start.year // start sate year
    val endYear = end.year // end date year

    return when {
        // Basically figures out how to format the end result date
        // If the years are NOT the same, it shows as "Month day-th, Year" for both
        // If the years ARE the same, but the months are different, then it does "Month day-th" for both
        // If the Year AND Month are the same, it just does "Month Day1 - Day2"
        startYear != endYear ->
            "${start.format(monthDayFormat)}, $startYear – " + "${end.format(monthDayFormat)}, $endYear"
        startMonth == endMonth ->
            "$startMonth $startDay–$endDay"

        else ->
            "${start.format(monthDayFormat)} – ${end.format(monthDayFormat)}"
    }
}