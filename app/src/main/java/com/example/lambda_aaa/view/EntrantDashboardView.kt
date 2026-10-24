package com.example.lambda_aaa.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.itemsIndexed
import com.example.lambda_aaa.model.Campsite

private val TrailCanvas = Color(0xFFF3F0E9)
private val TrailInk = Color(0xFF242823)
private val TrailMuted = Color(0xFF6E716C)
private val TrailBorder = Color(0xFFDEDCD4)
private val TrailSage = Color(0xFFE1EADF)
private val TrailSageDark = Color(0xFF526B5A)
private val TrailThumb = Color(0xFFE7ECE4)
private enum class TrailTab(val label: String) {
    Explore("Explore"),
    Lotteries("Lotteries"),
    Trips("Trips"),
    Profile("Profile"),
}

@Composable
fun EntrantDashboardView(
    navController: NavController,
    modifier: Modifier = Modifier,
    campsiteList: List<Campsite>,
    onNotificationsClick: () -> Unit = {},
    onFiltersClick: () -> Unit = {},
    onTabSelected: (String) -> Unit = {},

    ) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(TrailTab.Explore) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = TrailCanvas,
        bottomBar = {
            TrailDrawBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    onTabSelected(tab.label)
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TrailCanvas)
                .padding(innerPadding),
        ) {
            TrailDrawTopBar(onNotificationsClick = onNotificationsClick)
            HorizontalDivider(color = TrailBorder, thickness = 1.dp)

            // This Column encapsulates the rest of the Screen
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // This one covers the Search Bar and the text boxes above it
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Text(
                        text = "ENTRANT · DISCOVER",
                        color = TrailMuted,
                        fontSize = 10.sp,
                        letterSpacing = 0.75.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 20.dp),
                    )
                    Text(
                        text = "Find your next campsite",
                        color = TrailInk,
                        fontSize = 16.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 3.dp),
                    )

                    TrailDrawSearchBar(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        FilterPill(
                            label = "Jasper",
                            modifier = Modifier.weight(0.95f),
                        )
                        FilterPill(
                            label = "May 14–16",
                            modifier = Modifier.weight(1.22f),
                        )
                        FilterPill(
                            label = "4 guests",
                            modifier = Modifier.weight(1.10f),
                        )
                        FilterPill(
                            label = "Filters",
                            icon = Icons.Filled.Tune,
                            iconAboveText = true,
                            modifier = Modifier.weight(0.95f),
                            onClick = onFiltersClick,
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 17.dp, bottom = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "${campsiteList.size} campsites",
                            color = TrailInk,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Best match",
                            color = TrailMuted,
                            fontSize = 11.sp,
                        )
                    }

                    LazyColumn(modifier = modifier.fillMaxSize()) {
                        itemsIndexed(campsiteList) { _ , campsite ->
                            CampsiteCard(
                                campsite = campsite,
                                onClick = { }, // PUT CLICK ACTION HERE
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun TrailDrawTopBar(onNotificationsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Terrain,
                contentDescription = null,
                tint = TrailInk,
                modifier = Modifier.size(19.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Campsite Entrant",
                color = TrailInk,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Surface(
            onClick = onNotificationsClick,
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, TrailBorder),
            modifier = Modifier.size(40.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.NotificationsNone,
                    contentDescription = "Notifications",
                    tint = TrailInk,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

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
        border = BorderStroke(1.dp, TrailBorder),
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
                tint = TrailInk,
                modifier = Modifier.size(19.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = TrailInk,
                    fontSize = 14.sp,
                ),
                cursorBrush = SolidColor(TrailSageDark),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = "Search Campsites",
                                color = TrailMuted,
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
private fun FilterPill(
    label: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    icon: ImageVector? = null,
    iconAboveText: Boolean = false,
    onClick: () -> Unit = {},
) {
    val backgroundColor = if (selected) TrailSage else Color.White
    val borderColor = if (selected) TrailSageDark else TrailBorder

    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(13.dp),
        color = backgroundColor,
        contentColor = TrailInk,
        border = BorderStroke(if (selected) 1.2.dp else 1.dp, borderColor),
    ) {
        if (icon != null && iconAboveText) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 3.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TrailInk,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = label,
                    color = TrailInk,
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    maxLines = 1,
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 5.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = TrailInk,
                    fontSize = 11.sp,
                    maxLines = 1,
                    softWrap = false,
                )
            }
        }
    }
}

@Composable
private fun CampsiteCard(
    campsite: Campsite,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp),
        shape = RoundedCornerShape(17.dp),
        color = Color.White,
        border = BorderStroke(1.dp, TrailBorder),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = campsite.name,
                    color = TrailInk,
                    fontSize = 14.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${campsite.mapLocation} · ${campsite.maxOccupancy} guests",
                    color = TrailMuted,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    maxLines = 1,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (campsite.ratingCount != 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Rating",
                                tint = TrailMuted,
                                modifier = Modifier.size(12.dp),
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = campsite.ratingAverage.toString(),
                                color = TrailMuted,
                                fontSize = 10.5.sp,
                                maxLines = 1,
                            )
                        }
                    } else {
                        Text(
                            text = "No ratings",
                            color = TrailMuted,
                            fontSize = 10.5.sp,
                            maxLines = 1,
                        )
                    }
                    Text(
                        text = $$"$HellaBread&17Cent", //campsite.pricePerNight.toString(),
                        color = TrailMuted,
                        fontSize = 10.5.sp,
                        maxLines = 1,
                    )

                    if (campsite.cancellationPolicy != "") {
                        Text(
                            text = campsite.cancellationPolicy,
                            color = TrailMuted,
                            fontSize = 10.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))
            Surface(
                shape = RoundedCornerShape(13.dp),
                color = TrailThumb,
                modifier = Modifier.size(width = 68.dp, height = 56.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = TrailSageDark,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun TrailDrawBottomBar(
    selectedTab: TrailTab,
    onTabSelected: (TrailTab) -> Unit,
) {
    Surface(color = Color.White, shadowElevation = 0.dp) {
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(color = TrailBorder, thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(62.dp)
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TrailTab.entries.forEach { tab ->
                    val selected = tab == selectedTab
                    val icon = when (tab) {
                        TrailTab.Explore -> Icons.Filled.Explore
                        TrailTab.Lotteries -> Icons.Filled.ConfirmationNumber
                        TrailTab.Trips -> Icons.Filled.EventAvailable
                        TrailTab.Profile -> Icons.Filled.PersonOutline
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clickable { onTabSelected(tab) }
                            .padding(top = 7.dp, bottom = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.label,
                            tint = if (selected) TrailSageDark else TrailMuted,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = tab.label,
                            color = if (selected) TrailSageDark else TrailMuted,
                            fontSize = 10.sp,
                            lineHeight = 12.sp,
                            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}
