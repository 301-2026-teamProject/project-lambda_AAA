package com.example.lambda_aaa.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Tune
//import androidx.compose.material.icons.filled.WbTwilight // This was one of the icons the mockup uses so don't touch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.KeyboardType
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.controller.SearchController
import com.example.lambda_aaa.model.CampsiteRepository
//import com.example.lambda_aaa.model.LotteryRepository


private val Background = Color(0xFFF3F0E9) // Used for the background and Campsite icon. Bone-White color.
private val HeaderColor = Color(0xFF242823) // Used for Headers. Muted Black.
private val SubText = Color(0xFF6E716C) // Used for smaller text and Icons. Dark Gray.
private val BorderColor = Color(0xFFDEDCD4) // Used for borders. A muted equivalent of the Background color.
private val FilterSelectColor = Color(0xFFE1EADF) // Color chosen for filter boxes. A darker version of the Background color.
private val SelectColor = Color(0xFF526B5A) // Color chosen for other selected elements. A nice Green.



/**
 * Prints the Dashboard view for Campsite Entrants.
 *
 * Contains a notification tab, multiple pages, searchable campsites, campsite filters,
 * selectable campsites that can be selected (and whose lotteries you can join), etc.
 *
 * @param navController the controller that lets the user move to different pages
 * @return nothing
 */
@Composable
fun EntrantDashboardView(
    navController: NavController,
    onCampsiteClick: (String) -> Unit = {},
    ) {

    val repo = remember { SearchController( CampsiteRepository() ) } // Database
    val campsiteList = repo.campsites
    val filteredCampsites = repo.filteredCampsites
    val locations = campsiteList.map{ it.address }.distinct() // Gets SPECIFICALLY the general location of a campsite, like Jasper or Banff
    var searchQuery by remember { mutableStateOf("") }
    var selectedLocation by remember {mutableStateOf<String?>(null)}
    var selectedStartDate by remember { mutableStateOf<String?>(null) }
    var selectedEndDate by remember { mutableStateOf<String?>(null) }
    var selectedGuestCount by remember { mutableStateOf<Int?>(null) }
    var showFiltersDialog by remember { mutableStateOf(false) }
    repo.filterCampsite(searchQuery, selectedLocation, selectedGuestCount, selectedStartDate, selectedEndDate) // Runs every time the values change, letting us dynamically filter things

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(innerPadding),
        ) {
            TopBar()
            HorizontalDivider(color = BorderColor, thickness = 1.dp)

            // This Column encapsulates the rest of the Screen
            // Also the ENTIRE THING is scrollable so it can be used horizontally as well :)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Text(
                        text = "ENTRANT · DISCOVER",
                        color = SubText,
                        fontSize = 10.sp,
                        letterSpacing = 0.75.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 20.dp),
                    )
                    Text(
                        text = "Find your next campsite",
                        color = HeaderColor,
                        fontSize = 16.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 3.dp),
                    )

                    // Uses our custom function since it fits better that way
                    TrailDrawSearchBar(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                    )

                    // Displays all the filters (to be fixed soon)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        // Displays all the filter tabs
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Prints the Location Filter
                            if (selectedLocation != null) {
                                FilterBox(
                                    label = selectedLocation!!,
                                    onRemove = { selectedLocation = null },
                                )
                            }
                            // Prints the Date Filter
                            if (selectedStartDate != null && selectedEndDate != null) {
                                FilterBox(
                                    label = formatDateRangeLabel(selectedStartDate!!, selectedEndDate!!),
                                    onRemove = {
                                        selectedStartDate = null
                                        selectedEndDate = null
                                    },
                                )
                            }
                            // Prints the Guest Filter
                            if (selectedGuestCount != null) {
                                val count = selectedGuestCount!!
                                FilterBox(
                                    label = "$count ${if (count == 1) "guest" else "guests"}",
                                    onRemove = { selectedGuestCount = null },
                                )
                            }
                        }
                        FilterButton(
                            onClick = {
                                showFiltersDialog = true
                            },
                        )
                    }

                    // Row that displays the number of campsites (and the 'Best match' text)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 17.dp, bottom = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "${filteredCampsites.size} campsites",
                            color = HeaderColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Best match",
                            color = SubText,
                            fontSize = 11.sp,
                        )
                    }

                    // List of all filtered campsites
                    // Not LazyColumn because the screen is already scrollable
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        filteredCampsites.forEach { campsite ->
                            CampsiteCard(
                                campsite = campsite, // just sends the entire object which might not be efficient, but it works
                                onClick = { onCampsiteClick(campsite.cid) },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
    if (showFiltersDialog) {
        // Toggles the popup for selecting filters
        CampsiteFiltersDialog(
            locations = locations,
            initialLocation = selectedLocation,
            initialStartDate = selectedStartDate,
            initialEndDate = selectedEndDate,
            initialGuestCount = selectedGuestCount,
            onDismiss = { showFiltersDialog = false },
            onApply = { location, startDate, endDate, guestCount ->
                selectedLocation = location
                selectedStartDate = startDate
                selectedEndDate = endDate
                selectedGuestCount = guestCount
                showFiltersDialog = false
            },
        )
    }

}

/**
 * Container that shows the user's role, and provides a notification button.
 *
 * Pressing the button will open the notification tab.
 *
 * @return nothing
 */
@Composable
private fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // This is just the thing that states your role
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Terrain,
                contentDescription = null,
                tint = HeaderColor,
                modifier = Modifier.size(19.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Campsite Entrant",
                color = HeaderColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(modifier = Modifier.weight(1f))

        // The notification button
        Surface(
            //onClick = onNotificationsClick, // IMPLEMENT THIS NOTIFICATION BUTTON LATER
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.size(40.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.NotificationsNone,
                    contentDescription = "Notifications",
                    tint = HeaderColor,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

/**
 * Creates a searchbar that lets users type in the name of a Campsite.
 *
 * Uses a MutableStateOf string so that it updates in real time.
 *
 * @param value the current text within the textbox, which updates as the user types
 * @param onValueChange updates the menu on value change, dynamically updating the search bar
 * @param modifier used to adjust the dimensions of the bar on screen
 * @return nothing
 */
@Composable
private fun TrailDrawSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(15.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BorderColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = HeaderColor,
                modifier = Modifier.size(19.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = HeaderColor,
                    fontSize = 14.sp,
                ),
                cursorBrush = SolidColor(SelectColor),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = "Search Campsites",
                                color = SubText,
                                fontSize = 14.sp,
                            )
                        }
                        innerTextField()
                    }
                },
            )
        }
    }
}

/**
 * Creates a visibly applied filter for the listed campsites.
 *
 * The filter highlights what kind of filter it is (either location, time, or occupancy),
 * and can be removed by clicking on the filter.
 *
 * @param onRemove the function to run when a filter is clicked (used to remove it)
 * @return nothing
 */
@Composable
private fun FilterBox(
    label: String,
    onRemove: () -> Unit,
) {
    Surface(
        onClick = onRemove, // Click to remove
        shape = RoundedCornerShape(13.dp),
        color = FilterSelectColor,
        contentColor = HeaderColor,
        border = BorderStroke(1.dp, SelectColor.copy(alpha = 0.65f)), // I use a different color to make it more distinct
        modifier = Modifier.height(48.dp),
    ) {
        // Used to highlight the Text and 'x' icon
        Row(
            modifier = Modifier.padding(horizontal = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(
                text = label,
                color = HeaderColor,
                fontSize = 11.sp,
                maxLines = 1,
                softWrap = false,
            )
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Remove $label filter",
                tint = SelectColor,
                modifier = Modifier.size(13.dp),
            )
        }
    }
}

/**
 * Creates a button used to open up the filter tab.
 *
 * Is positioned to the side of all removable filters.
 *
 * @param onClick the function to run when this icon is clicked (used to open the filter tab)
 * @return nothing
 */
@Composable
private fun FilterButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick, // Clickable surface because regular buttons look bad
        modifier = Modifier.size(width = 56.dp, height = 48.dp),
        shape = RoundedCornerShape(13.dp),
        color = Color.White,
        contentColor = HeaderColor,
        border = BorderStroke(1.dp, BorderColor),
    ) {
        // Positions the text and icon neatly
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Tune,
                contentDescription = "Button Icon",
                tint = HeaderColor,
                modifier = Modifier.size(16.dp),
            )
            Text(text = "Filters", color = HeaderColor, fontSize = 10.sp, lineHeight = 12.sp)
        }
    }
}

/**
 * Creates a popup that lets the user filter the campsites they see.
 *
 * Once the popup is opened, the user may input a location (from a drop-down list),
 * select a range of dates (to check for availability), and input a number of guests.
 * Afterward, the user can either apply their changes or cancel them.
 *
 * @param locations the list of locations that are available to be selected from
 * @param initialLocation used to hold the location filter if it has already been created
 * @param initialStartDate used to hold the starting date filter if it has already been created
 * @param initialEndDate used to hold the ending date filter if it has already been created
 * @param initialGuestCount used to hold the guest count filter if it has already been created
 * @param onDismiss the function to cancel any withstanding filter changes
 * @param onApply the function to apply any withstanding filter changes
 * @return nothing
 */
@Composable
private fun CampsiteFiltersDialog(
    locations: List<String>,
    initialLocation: String?,
    initialStartDate: String?,
    initialEndDate: String?,
    initialGuestCount: Int?,
    onDismiss: () -> Unit,
    onApply: (location: String?, startDate: String?, endDate: String?, guests: Int?) -> Unit,
) {
    var draftLocation by remember { mutableStateOf(initialLocation) }
    var startDate by remember { mutableStateOf(initialStartDate.orEmpty()) }
    var endDate by remember { mutableStateOf(initialEndDate.orEmpty()) }
    var guestCountText by remember { mutableStateOf(initialGuestCount?.toString().orEmpty()) }
    var guestError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }

    // Adapted from Kotlin Foundation, "AlertDialog" API reference.
    // https://kotlinlang.org/api/compose-multiplatform/material3/androidx.compose.material3/-alert-dialog.html
    // Retrieved 2026-10-24
    // Changes: None, I just used this as a reference so I can make a 'pop-up' tab to select filters
    AlertDialog(
        onDismissRequest = onDismiss, // Used to exit the tab
        containerColor = Color.White,
        shape = RoundedCornerShape(22.dp),
        title = {
            Text(
                text = "Filters",
                color = HeaderColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )
        },
        text = {
            // I put all the user-inputted filters here because it somehow works under the text section hahaha
            // This column is used to store the inputted Location
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 560.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                Text(
                    text = "Location",
                    color = HeaderColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )

                // Custom menu that displays all options and lets you pick one
                // This is used to pick a Location
                DropdownSelector(
                    value = draftLocation?: "Any location", // If an option isn't selected, it says "Any Location" by default
                    options = listOf("Any location") + locations, // Easy way to include this is an option without adding it to the list of locations
                    onSelected = { choice ->
                        draftLocation = if (choice != "Any location") { // Checks location option
                            choice // if you selected a location, it makes a filter box
                        } else {
                            null // if you selected 'Any location' it DOESN'T create a new filter box
                            // This is so you don't filter by a location called 'Any location'
                        }
                    },
                )

                // Used for the date options
                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text(
                        text = "Available dates (optional)",
                        color = HeaderColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Adapted from Kotlin Foundation, "OutlinedTextField" API reference.
                        // https://kotlinlang.org/api/compose-multiplatform/material3/androidx.compose.material3/-outlined-text-field.html
                        // Retrieved 2026-10-24
                        // Changes: None, I just used this as a reference to make the outlined text fields correctly
                        // The before date
                        OutlinedTextField(
                            value = startDate,
                            onValueChange = {
                                startDate = it
                                dateError = null // if you change the input, the error gets disabled
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("Start date", fontSize = 11.sp) },
                            singleLine = true,
                            isError = dateError != null, // turns the textbox red if the user inputted something incorrect
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                            textStyle = TextStyle(fontSize = 12.sp, color = HeaderColor),
                        )
                        // The after date
                        OutlinedTextField(
                            value = endDate,
                            onValueChange = {
                                endDate = it
                                dateError = null // if you change the input, the error gets disabled
                            },
                            modifier = Modifier.weight(1f),
                            label = { Text("End date", fontSize = 11.sp) },
                            singleLine = true,
                            isError = dateError != null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                            textStyle = TextStyle(fontSize = 12.sp, color = HeaderColor),
                        )
                    }
                    // Just used to guarantee the user inputs a correct string
                    Text(
                        text = dateError?: "Enter dates as YYYY-MM-DD, for example 2026-11-04.",
                        color = if (dateError != null) MaterialTheme.colorScheme.error else SubText,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                    )
                }

                // Used to input the number of guests
                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text(
                        text = "Number of guests",
                        color = HeaderColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    OutlinedTextField(
                        value = guestCountText,
                        onValueChange = { input ->
                            // Adapted from Kotlin Foundation, "filter" API reference.
                            // https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/filter.html
                            // Retrieved 2026-10-24
                            // Changes: Changed so that user inputs can only be integers
                            guestCountText = input.filter { it.isDigit() }
                            guestError = null // if you change the input, the error gets disabled
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Number of guests", fontSize = 12.sp) },
                        singleLine = true,
                        isError = guestError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(fontSize = 14.sp, color = HeaderColor),
                    )
                    Text(
                        text = guestError?: "Enter a whole number between 1 and 99, or leave blank for any number.",
                        color = if (guestError != null) MaterialTheme.colorScheme.error else SubText,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                    )
                }
            }
        },
        // The "Apply" button
        confirmButton = {
            TextButton(
                onClick = {
                    // Has to check if everything the user typed is valid
                    val guestInput = guestCountText.trim()

                    val parsedGuestCount = if (guestInput.isEmpty()) {
                        null // If the inputted string is empty then we need it to be 'null'
                    } else {
                        // Adapted from Kotlin Foundation, "toIntOrNull" API reference.
                        // https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/to-int-or-null.html
                        // Retrieved 2026-10-24
                        // Changes: Changed to check if an input is a valid integer
                        guestInput.toIntOrNull() // Otherwise we can see if it's an int or not
                    }

                    if (guestInput.isNotEmpty() && parsedGuestCount == null || parsedGuestCount != null && parsedGuestCount < 1 || parsedGuestCount != null && parsedGuestCount > 99) {
                        // Basically if the user DID input something, but it's not an int, we raise an error
                        // Or if it IS an int and out of bounds (i.e. less than 1 or more than 99)
                        guestError = "Enter a valid whole number."
                    }

                    val start = startDate.trim()
                    val end = endDate.trim()
                    if (start.isEmpty() && end.isEmpty() && guestError == null) { // I.e. no errors and no date input
                        onApply(draftLocation, null, null, parsedGuestCount) // We apply it
                    } else if (start.isEmpty() && end.isEmpty() && guestError != null) {
                        guestError = "Enter a valid whole number."
                    } else if (start.isEmpty() || end.isEmpty()) { // If one is empty but not the other, then that's an error
                        dateError = "Enter both a start date and an end date."
                    } else {
                        val parsedStart = parseStrictIsoDate(start) // We have to use our function to parse it
                        val parsedEnd = parseStrictIsoDate(end)
                        when {
                            parsedStart == null || parsedEnd == null -> {
                                dateError = "Enter valid dates in YYYY-MM-DD format."
                            }
                            parsedStart.after(parsedEnd) -> {
                                dateError = "Start date must be on or before the end date."
                            }
                            // If it reaches here, then the dates are valid, but we have to check the guest number
                            guestError == null -> {
                                onApply(draftLocation, start, end, parsedGuestCount)
                            }
                        }
                    }
                },
            ) {
                Text("Apply", color = SelectColor, fontWeight = FontWeight.SemiBold)
            }
        },
        // Cancels any filtering changes
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SubText)
            }
        },
    )
}

/**
 * Creates dropdown selection menu.
 *
 * Starts in an un-expanded state by default.
 * Once pressed, all the options of the options are then revealed.
 * A user can press one of the options to be selected, which in turn, closes the menu.
 *
 * @param value the text shown on the textbox (i.e. the current selection)
 * @param options a list of all possible options that can be selected from
 * @param onSelected the function to run when an option is selected (returns to previous function)
 * @return nothing
 */
@Composable
private fun DropdownSelector(
    value: String,
    options: List<String>,
    onSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        // This part is just the textbox that you click to open the list of options
        Surface(
            onClick = { expanded = true }, // Shows the options when clicked :)
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 13.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = value,
                    color = HeaderColor,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f),
                )
                // Listen I'll avoid using Icons when necessary
                // I can get away with ASCII here so I might as well
                Text(text = "▾", color = SubText, fontSize = 16.sp)
            }
        }
        // Adapted from Android Developers, "DropdownMenu" API reference.
        // https://developer.android.com/reference/kotlin/androidx/compose/material3/DropdownMenu.composable
        // Retrieved 2026-10-24.
        // Changes: Adapted the Kotlin Code to turn it into a selection menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White),
        ) {
            // Goes through our list of 'options' (a list) and makes a selectable box for each
            options.distinct().forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = 13.sp) },
                    onClick = {
                        onSelected(option) // Selects the options pressed
                        expanded = false
                    },
                )
            }
        }
    }
}

/**
 * Checks if an inputted date matches the exact YYYY-MM-DD format
 *
 * Parses a given date to check whether it follows the YYYY-MM-DD format
 * and checks whether it is valid (i.e. not an impossible dates)
 *
 * @param value an inputted string that wil be checked to see if it is formatted as a date
 * @return Date?
 */
private fun parseStrictIsoDate(value: String): Date? {
    // Adapted from Kotlin Foundation, "Regex" API reference.
    // https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/-regex/
    // Retrieved 2026-10-25
    // Changes: None, used to see how this function works

    // Adapted from Android Developers, "Pattern" API reference.
    // https://developer.android.com/reference/java/util/regex/Pattern
    // Retrieved 2026-10-25
    // Changes: Adapted to fit my string parameters
    if (!Regex(pattern = "\\d{4}-\\d{2}-\\d{2}").matches(value)) { // the \\d is necessary because of word boundaries
        // Basically if our string doesn't match the "[4 integers]-[2 integers]-[2 integers]" pattern, then it fails
        return null
    }

    return try {
        // Adapted from Oracle Developers, "SimpleDateFormat" & "DateFormat" API reference.
        // https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html & https://docs.oracle.com/javase/8/docs/api/java/text/DateFormat.html
        // Retrieved 2026-10-25
        // Changes: None, used to figure out how to validify a date (i.e. so we don't accidentally pass feb. 30th)

        // Adapted from Android Developers, "SimpleDateFormat" API reference.
        // https://developer.android.com/reference/kotlin/java/text/SimpleDateFormat
        // Retrieved 2026-10-25
        // Changes: None, used to understand the Kotlin functions for this function
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { // the error handler recommended me to add the locale bit
            isLenient = false // the oracle page says this can mess with how exact a date, so I'm playing it safe here
        }
        // From there, we have to parse the date to turn it into a Date value that the system can actually read
        val position = ParsePosition(0)
        val parsed = formatter.parse(value, position) // Used to parse the date, only becomes null if it failed, otherwise it worked
        if (parsed != null && position.index == value.length && formatter.format(parsed) == value) {
            // I.e. if the parsing worked, and we parsed the entire thing, and reverse-parsing it gives us the same result, then it works
            parsed // If this all works, then the try returns this value of type Date
        } else {
            null // Otherwise it sends back nothing
        }
    } catch (_: IllegalArgumentException) { // try commands need us to catch errors
        null // returns null if the user typed in anything weird
    }
}

/**
 * Turns a Date value into a readable text
 *
 * A date of the format YYYY-MM-DD will turn into a value following the format "Month day-th, Year"
 *
 * @param start an inputted string that wil be checked to see if it is formatted as a date
 * @param end
 * @return String
 */
private fun formatDateRangeLabel(start: String, end: String): String {
    val startDate = parseStrictIsoDate(start) ?: return "$start – $end"
    val endDate = parseStrictIsoDate(end) ?: return "$start – $end"
    // Both of these function try to parse their respective dates, otherwise they just return the YYYY-MM-DD of both together

    fun dateFormatter(pattern: String) = SimpleDateFormat(pattern, Locale.US) // Helper function that formats each day patteran according to US time standards
    val monthDayFormat = dateFormatter("MMM d") // Formats dates such as 'Mar 7th'
    val yearFormat = dateFormatter("yyyy") // Format for a four digit year
    val startMonth = dateFormatter("MMM").format(startDate) // Abbreviated form of a start month
    val endMonth = dateFormatter("MMM").format(endDate) // Same but for the end day
    val startDay = dateFormatter("d").format(startDate) // Start day
    val endDay = dateFormatter("d").format(endDate) // End day
    val startYear = yearFormat.format(startDate) // start sate year
    val endYear = yearFormat.format(endDate) // end date year

    return when {
        // Basically figures out how to format the end result date
        // If the years are NOT the same, it shows as "Month day-th, Year" for both
        // If the years ARE the same, but the months are different, then it does "Month day-th" for both
        // If the Year AND Month are the same, it just does "Month Day1 - Day2"
        startYear != endYear -> "${monthDayFormat.format(startDate)}, $startYear – ${monthDayFormat.format(endDate)}, $endYear"
        startMonth == endMonth -> "$startMonth $startDay–$endDay"
        else -> "${monthDayFormat.format(startDate)} – ${monthDayFormat.format(endDate)}"
    }
}
