package com.example.lambda_aaa.view

import android.R.id.message
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.lambda_aaa.controller.CampsiteDetailController
import com.example.lambda_aaa.controller.CampsiteManagementController
import com.example.lambda_aaa.model.CampsiteRepository
import androidx.navigation.NavType
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * Main app logic */
@Composable
fun CampsiteReservationApp(
    uid: String,
) {
    val navController = rememberNavController()
    val campsiteRepository = remember { CampsiteRepository() }
    val msg = ""

    NavHost(navController = navController, startDestination = "ownerHome/$msg") {
        composable(
            route = "ownerHome/{success}/{msg}",
            arguments = listOf(
                navArgument("success") { type = NavType.StringType },
                navArgument("msg") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            CampsiteOwnerView(
                uid = uid,
                controller = CampsiteDetailController(
                    uid = uid,
                    ownerSearch = true,
                    repository = campsiteRepository
                ),
                onClickRegister = { navController.navigate(("registerScreen")) },
                onClickCampsite = { cid -> navController.navigate("detailScreen/$cid") },
                success = backStackEntry.arguments?.getBoolean("success") ?: false,
                message = backStackEntry.arguments?.getString("msg") ?: return@composable
            )
        }
        composable("registerScreen") {
            CampsiteEditorView(
                isNew = true,
                onDone = { success, msg -> navController.navigate("ownerHome/$success/$msg") },
                onBack = { navController.popBackStack() },
                controller = CampsiteManagementController(
                    cid = campsiteRepository.newCampsiteId(),
                    uid = uid,
                    isNew = true,
                    repository = campsiteRepository
                )
            )
        }
        composable(
            route = "detailScreen/{cid}",
            arguments = listOf(navArgument("cid") { type = NavType.StringType } )
        ) { backStackEntry ->
            val cid = backStackEntry.arguments?.getString("cid") ?: return@composable

            CampsiteDetailView(
                cid = cid,
                onClickEdit = { navController.navigate("editScreen/$cid") },
                onBack = { navController.popBackStack() },
                controller = CampsiteDetailController(
                    uid = uid,
                    ownerSearch = true,
                    repository = campsiteRepository
                )
            )
        }
        composable(
            route = "editScreen/{cid}",
            arguments = listOf(navArgument("cid") { type = NavType.StringType })
        ) { backStackEntry ->
            CampsiteEditorView(
                isNew = false,
                onDone = { success, msg -> navController.navigate("ownerHome/$success/$msg") },
                onBack = { navController.popBackStack() },
                controller = CampsiteManagementController(
                    cid = backStackEntry.arguments?.getString("cid") ?: return@composable,
                    uid = uid,
                    isNew = false,
                    repository = campsiteRepository
                )
            )
        }
    }
}