package com.example.lambda_aaa.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.remember
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lambda_aaa.controller.AdminRatingController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.res.stringResource
import com.example.lambda_aaa.R
import androidx.compose.material3.SnackbarHostState

@Composable
fun AdminRatingView(
    controller: AdminRatingController,
    admin: Boolean,
    onBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column {
            AdminTopBar(onBack = onBack)

            when {
                controller.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }

                controller.errorMessage != null -> Column(
                    Modifier.fillMaxSize().padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(controller.errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                    Button(onClick = controller::refresh) { Text(stringResource(R.string.retry)) }
                }

                controller.ratings.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(
                        "This campsite doesn't have a rating yet.",
                        color = SubText
                    )
                }

                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(controller.ratings, key = { it.rid }) {
                        RatingCard(
                            rating = it,
                            admin = admin,
                            onClick = { rid ->
                                controller.refresh()
                                controller.removeRating(rid) { success, msg ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = if (success) "Successfully removed rating!" else "Failed to remove rating: $msg",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminTopBar(
    onBack: () -> Unit
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
                text = "Ratings and Reviews",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = HeaderColor,
                modifier = Modifier.align(Alignment.Center)
            )

            // Thin bottom border
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BorderColor)
            )
        }
    }
}