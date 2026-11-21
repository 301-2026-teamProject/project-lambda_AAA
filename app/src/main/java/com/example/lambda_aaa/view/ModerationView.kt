package com.example.lambda_aaa.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lambda_aaa.controller.SearchController
import com.example.lambda_aaa.helper.formatDateRangeLabel
import com.example.lambda_aaa.model.CampsiteRepository
import java.time.LocalDate

@Composable
fun ModerationView (
    navController: NavController,
    browseOption: String? = null,
    onBack: () -> Unit = {},
    onCampsiteClick: (String) -> Unit = {},
) {

    val repo = remember { SearchController( CampsiteRepository() ) } // Database
    val filteredCampsites = repo.filteredCampsites
    var searchQuery by remember { mutableStateOf("") }
    var selectedLocation by remember {mutableStateOf<String?>(null)}
    var timeStartDate by remember { mutableStateOf<LocalDate?>(null) }
    var timeEndDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedGuestCount by remember { mutableStateOf<Int?>(null) }
    repo.filterCampsite(searchQuery, selectedLocation, selectedGuestCount, timeStartDate, timeEndDate) // Runs every time the values change, letting us dynamically filter things

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)

    ) {
        TopBar(
            onBack = onBack,
            onFilterClick = onBack
        )
        HorizontalDivider(color = BorderColor, thickness = 1.dp)

        // This Column encapsulates the rest of the Screen
        // Also the ENTIRE THING is scrollable so it can be used horizontally as well :)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
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
                        if (timeStartDate != null && timeEndDate != null) {
                            FilterBox(
                                label = formatDateRangeLabel(timeStartDate!!, timeEndDate!!),
                                onRemove = {
                                    timeStartDate = null
                                    timeEndDate = null
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


/**
 * Container that shows the user's role, and provides a notification button.
 *
 * Pressing the button will open the notification tab.
 *
 * @return nothing
 */
@Composable
private fun TopBar(
    onBack: () -> Unit,
    onFilterClick: () -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Color.White)
                .padding(horizontal = 16.dp)
        ) {
            // Back button
            Surface(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterStart),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = HeaderColor,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }


            // Centered title
            Text(
                text = "Campsites",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = HeaderColor,
                modifier = Modifier.align(Alignment.Center)
            )


            // Filter button
            Surface(
                onClick = onFilterClick,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterEnd),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FilterList,
                        contentDescription = "Filter",
                        tint = HeaderColor,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
        // Bottom divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BorderColor)
        )
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