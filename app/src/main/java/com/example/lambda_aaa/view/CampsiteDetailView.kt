package com.example.lambda_aaa.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lambda_aaa.R
import com.example.lambda_aaa.controller.CampsiteDetailController
import com.example.lambda_aaa.model.Campsite
import com.example.lambda_aaa.model.Lottery

@Composable
fun CampsiteDetailView(
    campsiteDetailController: CampsiteDetailController,
    onBack: () -> Unit,
    onEdit: (() -> Unit)? = null,
) {
    LaunchedEffect(campsiteDetailController) {
        campsiteDetailController.fetchCampsiteAndLottery()
    }
    CampsiteDetailContent(
        campsite = campsiteDetailController.campsite,
        isLoading = campsiteDetailController.isLoading,
        errorMessage = campsiteDetailController.errorMessage,
        onRetry = campsiteDetailController::fetchCampsiteAndLottery,
        onBack = onBack,
        onEdit = onEdit,
        lotteries = campsiteDetailController.lotteries,
        lotteriesLoading = campsiteDetailController.lotteriesLoading,
        lotteriesFailed = campsiteDetailController.lotteriesFailed,
        onRetryLotteries = campsiteDetailController::fetchLotteries,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun CampsiteDetailContent(
    campsite: Campsite?,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onEdit: (() -> Unit)? = null,
    lotteries: List<Lottery> = emptyList(),
    lotteriesLoading: Boolean = false,
    lotteriesFailed: Boolean = false,
    onRetryLotteries: () -> Unit = {},
) {
    val context = LocalContext.current
    Scaffold(
        containerColor = TrailCanvas,
        contentColor = TrailInk,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.campsite_details), style = MaterialTheme.typography.titleMedium) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White, titleContentColor = TrailInk),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back), tint = TrailInk)
                    }
                },
            )
        },
    ) { padding ->
        when {
            isLoading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator()
            }
            errorMessage != null || campsite == null -> Column(
                Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(errorMessage ?: stringResource(R.string.campsite_not_found))
                Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
            }
            else -> Column(
                Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Surface(shape = RoundedCornerShape(18.dp), color = TrailThumb) {
                    Box(Modifier.fillMaxWidth().heightIn(min = 180.dp)) {
                        CampsiteImage(campsite, Modifier.matchParentSize())
                        Column(
                            Modifier.fillMaxWidth().align(Alignment.BottomStart)
                                .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xDD243328))))
                                .padding(top = 36.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(campsite.name, color = Color.White, style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold, modifier = Modifier.semantics { heading() })
                            Text(campsite.address.ifBlank { stringResource(R.string.location_unavailable) }, color = Color.White)
                            Text(ratingText(campsite), color = Color.White)
                        }
                    }
                }
                Text(occupancyText(campsite), fontWeight = FontWeight.Medium)
                if (campsite.tags.isNotEmpty()) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        campsite.tags.forEach { tag ->
                            Surface(shape = RoundedCornerShape(12.dp), color = Color.White, contentColor = TrailInk,
                                border = BorderStroke(1.dp, TrailBorder)) {
                                Text(tag, Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                            }
                        }
                    }
                }
                DetailSection(stringResource(R.string.description), campsite.description)
                DetailSection(stringResource(R.string.rules_and_policy), campsite.rulesAndPolicy)
                DetailSection(stringResource(R.string.cancellation_policy), campsite.cancellationPolicy)
                CampsiteLotteriesSection(lotteries, lotteriesLoading, lotteriesFailed, onRetryLotteries)
                campsite.mapLocation?.let { point ->
                    OutlinedButton(
                        onClick = {
                            val location = "${point.latitude},${point.longitude}"
                            val uri = Uri.parse("geo:$location?q=" + Uri.encode("$location (${campsite.name})"))
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            } catch (_: ActivityNotFoundException) {
                                Toast.makeText(context, R.string.no_map_app, Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = TrailInk),
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.view_on_map))
                    }
                }
                onEdit?.let { edit ->
                    Button(onClick = edit, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.edit_campsite))
                    }
                }
            }
        }
    }
}

@Composable
internal fun CampsiteLotteriesSection(
    lotteries: List<Lottery>,
    isLoading: Boolean,
    failed: Boolean,
    onRetry: () -> Unit,
) {
    Surface(shape = RoundedCornerShape(14.dp), color = TrailSage, contentColor = TrailInk) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.campsite_lotteries), fontWeight = FontWeight.SemiBold,
                modifier = Modifier.semantics { heading() })
            when {
                isLoading -> {
                    CircularProgressIndicator()
                    Text(stringResource(R.string.lotteries_loading))
                }
                failed -> {
                    Text(stringResource(R.string.lotteries_load_failed))
                    TextButton(onClick = onRetry) { Text(stringResource(R.string.retry)) }
                }
                lotteries.isEmpty() -> Text(stringResource(R.string.no_campsite_lotteries))
                else -> lotteries.forEachIndexed { index, lottery ->
                    if (index > 0) HorizontalDivider()
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(stringResource(R.string.campsite_lottery_number, index + 1),
                            fontWeight = FontWeight.Medium)
                        Text(lottery.bookingPeriod?.let {
                            stringResource(R.string.lottery_booking_period, it.start.toString(), it.end.toString())
                        } ?: stringResource(R.string.lottery_booking_unavailable))
                        Text(lottery.registrationPeriod?.let {
                            stringResource(R.string.lottery_registration_period, it.start.toString(), it.end.toString())
                        } ?: stringResource(R.string.lottery_registration_unavailable))
                        Text(stringResource(R.string.lottery_winner_count, lottery.numWinners))
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSection(title: String, value: String) {
    Surface(shape = RoundedCornerShape(14.dp), color = Color.White, contentColor = TrailInk,
        border = BorderStroke(1.dp, TrailBorder)) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, modifier = Modifier.semantics { heading() })
            Text(value.ifBlank { stringResource(R.string.not_provided) })
        }
    }
}
