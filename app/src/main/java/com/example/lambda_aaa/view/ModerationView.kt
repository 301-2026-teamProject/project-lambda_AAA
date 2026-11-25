package com.example.lambda_aaa.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.lambda_aaa.model.CampsiteRepository
import com.example.lambda_aaa.model.UserProfile

@Composable
fun ModerationView (
    navController: NavController,
    repo: SearchController,
    browseStartOption: String = "Campsites",
    onBack: () -> Unit = {},
    onCardClick: (String, String) -> Unit,
) {

    val filteredCampsites = repo.filteredCampsites
    val listedProfiles = repo.profiles
    val filteredProfiles = repo.filteredProfiles
    var browseOption by remember { mutableStateOf(browseStartOption) }
    var listSizeDisplay by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    repo.filterCampsite(searchQuery, null, null, null, null) // Runs every time the values change, letting us dynamically filter things
    repo.filterProfiles(searchQuery) // Runs every time the values change, letting us dynamically filter things


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)

    ) {
        TopBar(
            onBack = onBack,
            browseOption,
            onApply = { choice ->
                browseOption = choice
            }
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
                    text = "Find Active $browseOption",
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

                Spacer(modifier = Modifier.height(14.dp))

                // Filter buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    FilterCard(
                        text = "All",
                        selected = selectedFilter == "All",
                        onClick = {
                            selectedFilter = "All"
                        }
                    )

                    FilterCard(
                        text = "Flagged",
                        selected = selectedFilter == "Flagged",
                        onClick = {
                            selectedFilter = "Flagged"
                        }
                    )

                    FilterCard(
                        text = "Removed",
                        selected = selectedFilter == "Removed",
                        onClick = {
                            selectedFilter = "Removed"
                        }
                    )
                }

                // Row that displays the number of campsites (and the 'Best match' text)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "$listSizeDisplay $browseOption",
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
                    when (browseOption) {
                        "Campsites" -> {
                            listSizeDisplay = filteredCampsites.size
                            filteredCampsites.forEach { campsite ->
                                val idMatch: UserProfile? = listedProfiles.firstOrNull { it.uid == campsite.ownerId }
                                AdminCampsiteCard(
                                    campsite = campsite, // just sends the entire object which might not be efficient, but it works
                                    ownerName = idMatch?.name,
                                    onClick = { onCardClick("Campsite", campsite.cid) },
                                )
                            }
                        }
                        "Profiles" -> {
                            listSizeDisplay = filteredProfiles.size
                            listedProfiles.forEach { profile ->
                                AdminProfileCard(
                                    profile = profile, // just sends the entire object which might not be efficient, but it works
                                    onClick = { onCardClick("Profile", profile.uid) },
                                )
                            }
                        }
                        "Images" -> {
                            listSizeDisplay = 0
                        }
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
    browseOption: String,
    onApply: (filterChoice: String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
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
                text = browseOption,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = HeaderColor,
                modifier = Modifier.align(Alignment.Center)
            )


            // Dropdown menu for searchable results
            Box(modifier = Modifier.align(Alignment.CenterEnd) ) {
                // This part is just the textbox that you click to open the list of options
                Surface(
                    onClick = { expanded = true },
                    modifier = Modifier.size(40.dp),
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
                    listOf("Campsites", "Profiles", "Images").distinct().forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, fontSize = 13.sp) },
                            onClick = {
                                onApply(option) // Selects the options pressed
                                expanded = false
                            },
                        )
                    }
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
                                text = "Search Database",
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

@Composable
private fun FilterCard(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(13.dp),
        color = if (selected) {
            SelectColor
        } else {
            Color.White
        },
        border = BorderStroke(
            1.dp,
            if (selected) SelectColor else BorderColor
        )
    ) {

        Box(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (selected) {
                    FontWeight.Medium
                } else {
                    FontWeight.Normal
                },
                color = HeaderColor
            )
        }
    }
}

