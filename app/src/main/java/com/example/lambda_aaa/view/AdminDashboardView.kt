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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lambda_aaa.controller.SearchController
import androidx.compose.material3.Button
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.DropdownMenu
import com.example.lambda_aaa.model.Role
import androidx.compose.material3.DropdownMenuItem

@Composable
fun AdminDashboardView (
    navController: NavController,
    repo: SearchController,
    roles: List<Role>,
    onSwitchRole: (Role) -> Unit = {},
    onOpenCard: (String) -> Unit = {},
    onOpenLogs: () -> Unit = {},
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Top navigation bar
        AdminTopBar(
            roles = roles,
            onSwitchRole = { role -> onSwitchRole(role) }
        )

        // Main scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {

            // Section heading
            Text(
                text = "MODERATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = SubText
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Review application content",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = HeaderColor
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Statistics - first row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    number = repo.fetchCampsiteNum().toString(),
                    label = "Campsites"
                )

                StatCard(
                    modifier = Modifier.weight(1f),
                    number = repo.fetchProfileNum().toString(),
                    label = "Profiles"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Second row of statistics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    number = "Placeholder",
                    label = "Images"
                )

                StatCard(
                    modifier = Modifier.weight(1f),
                    number = "Placeholder",
                    label = "Flagged items"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Browse heading
            Text(
                text = "Browse",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = HeaderColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Browse cards
            AdminBrowseCard(
                title = "Campsites",
                description = "Review and remove listings",
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Landscape,
                        contentDescription = null,
                        tint = HeaderColor,
                        modifier = Modifier.size(19.dp)
                    )
                },
                onClick = { onOpenCard("Campsites") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            AdminBrowseCard(
                title = "Profiles",
                description = "Review users and owner roles",
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.People,
                        contentDescription = null,
                        tint = HeaderColor,
                        modifier = Modifier.size(19.dp)
                    )
                },
                onClick = { onOpenCard("Profiles") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            AdminBrowseCard(
                title = "Images",
                description = "Review uploaded content",
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        tint = HeaderColor,
                        modifier = Modifier.size(19.dp)
                    )
                },
                onClick = { onOpenCard("Images") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Browse notification
            Text(
                text = "View History Notifications and Messages",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = HeaderColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Browse cards
            AdminBrowseCard(
                title = "Logs",
                description = "View History Notifications and Messages",
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = null,
                        tint = HeaderColor,
                        modifier = Modifier.size(19.dp)
                    )
                },
                onClick = onOpenLogs
            )
        }
    }
}

@Composable
private fun AdminTopBar(
    roles: List<Role>,
    onSwitchRole: (Role) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White)
                .padding(horizontal = 17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Outlined.Security,
                contentDescription = "Admin",
                tint = HeaderColor,
                modifier = Modifier.size(19.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Admin",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = HeaderColor
            )

            Spacer(modifier = Modifier.weight(1f))

            Box {
                Button(onClick = { expanded = true }) {
                    Text("Switch Role")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    roles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(
                                when (role) {
                                    Role.ENTRANT -> "Entrant"
                                    Role.OWNER -> "Owner"
                                    Role.ADMIN -> "Admin"
                                }
                            ) },
                            onClick = {
                                onSwitchRole(role)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        // Thin bottom border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BorderColor)
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    number: String,
    label: String
) {
    Surface(
        modifier = modifier.height(74.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 13.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = number,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = HeaderColor
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = label,
                fontSize = 12.sp,
                color = SubText
            )
        }
    }
}


@Composable
private fun AdminBrowseCard(
    title: String,
    description: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Left icon
            icon()

            Spacer(modifier = Modifier.width(12.dp))

            // Title and description
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HeaderColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = SubText
                )
            }

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "Open $title",
                tint = HeaderColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}