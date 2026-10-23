package com.example.lambda_aaa.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.lambda_aaa.controller.CampsiteDetailController
import com.example.lambda_aaa.controller.CampsiteManagementController
import com.example.lambda_aaa.model.CampsiteRepository

@Composable
fun CampsiteReservationApp() {
    val navController = rememberNavController()
    val campsiteRepository = CampsiteRepository()
    val cid = "123"

    NavHost(navController = navController, startDestination = "ownerHome") {
        composable("ownerHome") {
            CampsiteOwnerView(
                onClickRegister = { navController.navigate(("registerScreen")) },
                onClickCampsite = { navController.navigate("editScreen") }
            )
        }
        composable("registerScreen") {
            CampsiteEditorView(
                isNew = true,
                onDone = { },  // what happens after campsite is registered?
                onBack = { navController.navigate("ownerHome") },
                controller = CampsiteManagementController(
                    cid = campsiteRepository.newCampsiteId(),
                    isNew = true,
                    repository = campsiteRepository
                )
            )
        }
        composable("editScreen") {
            CampsiteEditorView(
                isNew = false,
                onDone = { },  // what happens after campsite is modified?
                onBack = { navController.navigate("ownerHome") },
                controller = CampsiteManagementController(
                    cid = cid,
                    isNew = false,
                    repository = campsiteRepository
                )
            )
        }
    }
}