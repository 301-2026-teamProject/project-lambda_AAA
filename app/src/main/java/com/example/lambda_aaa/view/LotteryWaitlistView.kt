/**
 * Code for implementing the Lottery Waitlist Screen as show on the GUI mockup found in GitHub
 */

package com.example.lambda_aaa.view

import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import java.util.UUID
import com.example.lambda_aaa.model.NotificationRepository
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardBackspace
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lambda_aaa.model.LotteryEntry
import com.example.lambda_aaa.model.LotteryForm
import com.example.lambda_aaa.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.LotteryRepository

/**
 * Implements the UI from the GitHub GUI mockup of Lottery Waitlist Screen
 *
 * @param lotteryWaitlistNavController the controller that handles navigation into and out of
 * LotteryWaitlistView
 */
@Composable
fun LotteryWaitlistView(
    lotteryWaitlistNavController: NavController,
    lotteryId: String,
    ownerId: String
) {

    var gotEntries by remember { mutableStateOf(true) }
    val entries = remember(lotteryId) { mutableStateListOf<LotteryEntry>() }
    val entrants = remember(lotteryId) { mutableStateListOf<String>() }
    val entriesStatus = remember(lotteryId) { mutableStateListOf<String>() }
    var numEntries by remember(lotteryId) { mutableIntStateOf(0) }
    var numWinners by remember(lotteryId) { mutableIntStateOf(0) }
    val lotteryDB = remember { LotteryRepository() }
    DisposableEffect(lotteryDB) {
        onDispose { lotteryDB.close() }
    }

    LaunchedEffect(lotteryId) {
        lotteryDB.getLottery(lotteryId) { success, lottery, msg ->
            if (success) {
                numWinners = lottery?.numWinners ?: 0
            } else {
                Log.e("Lottery Error", "Could not load lottery: $msg")
            }
        }

        // Search for entrants enrolled in lottery
        try {

            val waitlistSnapshot = Firebase.firestore.collection("lottery_entries")
                .whereEqualTo("lotteryId", lotteryId)
                .get()
                .await()

            // If there were entries, put them into a list
            if (!waitlistSnapshot.isEmpty) {
                numEntries = waitlistSnapshot.count()
                val lotteryEntries = waitlistSnapshot.toObjects(LotteryEntry::class.java)
                entries.addAll(lotteryEntries)

                // For each user in the waiting list, get their names and their status
                for (i in 0..<numEntries) {
                    try {

                        val userSnapshot = Firebase.firestore.collection("users")
                            .document(entries[i].userId)
                            .get()
                            .await()

                        // If there were entries, put them into a list
                        if (userSnapshot != null && userSnapshot.exists()) {
                            val currentEntrant = userSnapshot.toObject(UserProfile::class.java)!!
                            entrants.add(currentEntrant.name)
                            entriesStatus.add(entries[i].status.toString())
                        }

                    } catch (e: Exception) {
                        Log.e("Query Users Error", "Failed to fetch documents: ${e.message}", e)
                    }

                }
            }

        } catch (e: Exception) {
            Log.e("Query Lottery Entries Error", "Failed to fetch documents: ${e.message}", e)
        }

        gotEntries = !gotEntries

    }


    var showNotifyDialog by rememberSaveable { mutableStateOf(false) }
    val notificationRepo = remember { NotificationRepository() }
    val scope = rememberCoroutineScope()
    var broadcastId by rememberSaveable(lotteryId) { mutableStateOf("") }
    var sending by remember { mutableStateOf(false) }
    var sendError by rememberSaveable { mutableStateOf("") }
    var feedback by rememberSaveable { mutableStateOf("") }
    var notificationMessage by rememberSaveable { mutableStateOf("") }

    Scaffold(
        containerColor = Background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Background)
        ) {
            HorizontalDivider(color = BorderColor, thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .background(Color.White)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.size(40.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardBackspace,
                            contentDescription = "Backspace",
                            tint = HeaderColor,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Lottery Waitlist",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.size(40.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "Triple Dots",
                            tint = HeaderColor,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "REGISTRATION ENDED",
                                fontSize = 13.sp,
                                color = SubText,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "$numEntries entrants",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = FilterSelectColor,
                            contentColor = HeaderColor,
                            modifier = Modifier.fillMaxWidth(0.3f),
                        ) {
                            Text(
                                "$numWinners winners",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.width(10.dp)) }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = AddGreen,
                            border = BorderStroke(1.dp, BorderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White
                                )
                            ) { Text("Draw winners") }
                        }
                    }
                }


                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Entrants",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, BorderColor),
                            modifier = Modifier.fillMaxWidth(0.28f),
                        ) {
                            Text(
                                "All statuses",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }
                }

                itemsIndexed(entrants) { index, user ->

                    val userInitials = getInitials(user)

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, BorderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.background(
                                    color = FilterSelectColor,
                                    shape = CircleShape
                                )
                            ) {
                                Text(
                                    userInitials,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(5.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column() {

                                Text(
                                    user,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )

                                Text(
                                    entriesStatus[index],
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = SubText,
                                )

                            }
                        }

                    }
                }


                if (feedback.isNotEmpty()) item { Text(feedback) }
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, BorderColor),
                        modifier = Modifier.fillMaxWidth(1f)
                    ) {
                        Button(
                            onClick = { showNotifyDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )

                            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))

                            Text("Notify waitlist")
                        }
                    }
                }

            }


        }
    }

    if (showNotifyDialog) {
        AlertDialog(
            onDismissRequest = { if (!sending) showNotifyDialog = false },
            title = { Text(if (broadcastId.isEmpty()) "Notify waitlist" else "Confirm notification") },
            text = {
                Column {
                    Text("Recipients: entrants currently waiting for this lottery with notifications enabled.")
                    if (broadcastId.isEmpty()) {
                        OutlinedTextField(value = notificationMessage,
                            onValueChange = { notificationMessage = it }, label = { Text("Message") })
                    } else {
                        Text(notificationMessage.trim())
                    }
                    if (sendError.isNotEmpty()) Text(sendError)
                }
            },
            confirmButton = {
                TextButton(enabled = !sending && notificationMessage.isNotBlank(), onClick = {
                    if (broadcastId.isEmpty()) {
                        broadcastId = UUID.randomUUID().toString()
                    } else {
                        sending = true
                        sendError = ""
                        scope.launch {
                            try {
                                val count = notificationRepo.sendWaitlistBroadcast(
                                    ownerId, lotteryId, notificationMessage, broadcastId)
                                feedback = if (count == 0) "No eligible waitlist entrants."
                                    else "Notification sent to $count waitlist entrant(s)."
                                showNotifyDialog = false
                                notificationMessage = ""
                                broadcastId = ""
                            } catch (cancelled: CancellationException) {
                                throw cancelled
                            } catch (e: Exception) {
                                sendError = e.localizedMessage ?: "Could not finish sending."
                            } finally {
                                sending = false
                            }
                        }
                    }
                }) {
                    Text(when {
                        sending -> "Sending…"
                        broadcastId.isEmpty() -> "Preview"
                        sendError.isNotEmpty() -> "Retry"
                        else -> "Send"
                    })
                }
            },
            dismissButton = {
                TextButton(enabled = !sending, onClick = {
                    if (broadcastId.isNotEmpty() && sendError.isEmpty()) broadcastId = ""
                    else showNotifyDialog = false
                }) { Text(if (broadcastId.isNotEmpty() && sendError.isEmpty()) "Edit" else "Close") }
            }
        )
    }
}


/**
 * Helper function to extract the Entrant's initials for display
 *
 * @param name the name of the Entrant whose initials are to be extracted
 * @return the initials of the Entrant as a string of capital characters
 */
// removed private modifier otherwise it raises error
fun getInitials(name: String) : String {
    val cleanName = name.trim()
    val initials = cleanName.filter { it.isUpperCase() }
    return initials
}
