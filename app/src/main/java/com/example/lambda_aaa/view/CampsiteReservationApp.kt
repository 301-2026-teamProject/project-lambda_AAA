package com.example.lambda_aaa.view

import android.R.id.message
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.lambda_aaa.controller.CampsiteOwnerController
import com.example.lambda_aaa.controller.CampsiteManagementController
import com.example.lambda_aaa.model.CampsiteRepository
import androidx.navigation.NavType
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.lambda_aaa.controller.CampsiteDetailController

/**
 * Main app logic */
@Composable
fun CampsiteReservationApp(
    uid: String,
) {
    val navController = rememberNavController()
    val repo = remember { CampsiteRepository() }
    DisposableEffect(repo) { onDispose { repo.close() } }
    val ownerController = remember {
        CampsiteOwnerController(
            uid = uid,
            repository = repo
        )
    }

    var editMessage by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    fun goHome(success: Boolean, msg: String) {
        editMessage = success to msg
        navController.popBackStack("ownerHome", inclusive = false)
    }

    NavHost(navController, startDestination = "ownerHome") {
        composable("ownerHome") {
            CampsiteOwnerView(
                uid = uid,
                controller = ownerController,
                onClickRegister = { navController.navigate("registerScreen") },
                onClickCampsite = { cid -> navController.navigate("detailScreen/$cid") },
                editMessage = editMessage,
                onEditMessageShown = { editMessage = null }
            )
        }
        composable("registerScreen") {
            val controller = remember {
                CampsiteManagementController(repo.newCampsiteId(), uid, true, repo)
            }
            CampsiteEditorView(
                isNew = true,
                onDone = { ok, msg -> goHome(ok, msg) },
                onBack = { navController.popBackStack() },
                controller = controller
            )
        }
        composable(
            "detailScreen/{cid}",
            arguments = listOf(navArgument("cid") { type = NavType.StringType })
        ) { entry ->
            val cid = entry.arguments?.getString("cid") ?: return@composable
            val controller = remember(cid, repo) { CampsiteDetailController(cid = cid, repository = repo) }
            CampsiteDetailView(
                campsiteDetailController = controller,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate("editScreen/$cid") }
            )
        }
        composable(
            "editScreen/{cid}",
            arguments = listOf(navArgument("cid") { type = NavType.StringType })
        ) { entry ->
            val cid = entry.arguments?.getString("cid") ?: return@composable
            val controller = remember(cid) { CampsiteManagementController(cid, uid, false, repo) }
            CampsiteEditorView(
                isNew = false,
                onDone = { ok, msg -> goHome(ok, msg) },
                onBack = { navController.popBackStack() },
                controller = controller
            )
        }
    }
}