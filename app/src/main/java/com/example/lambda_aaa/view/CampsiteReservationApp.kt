package com.example.lambda_aaa.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.lambda_aaa.controller.CampsiteDetailController
import com.example.lambda_aaa.controller.CampsiteManagementController
import com.example.lambda_aaa.model.CampsiteRepository
import androidx.navigation.NavType

/**
 * Main app logic */
@Composable
fun CampsiteReservationApp() {
    val navController = rememberNavController()
    val campsiteRepository = CampsiteRepository()
    val uid = "123" // retrieve actual uid from auth repository upon login

    NavHost(navController = navController, startDestination = "ownerHome") {
        composable("ownerHome") {
            CampsiteOwnerView(
                onClickRegister = { navController.navigate(("registerScreen")) },
                onClickCampsite = { cid -> navController.navigate("editScreen/$cid") }
            )
        }
        composable("registerScreen") {
            CampsiteEditorView(
                isNew = true,
                onDone = { },  // what happens after campsite is registered?
                onBack = { navController.navigate("ownerHome") },
                controller = CampsiteManagementController(
                    cid = campsiteRepository.newCampsiteId(),
                    uid = uid,
                    isNew = true,
                    repository = campsiteRepository
                )
            )
        }
        composable(
            route = "editScreen",
            arguments = listOf(navArgument("cid") { type = NavType.StringType })
        ) { backStackEntry ->
            CampsiteEditorView(
                isNew = false,
                onDone = { },  // what happens after campsite is modified?
                onBack = { navController.navigate("ownerHome") },
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