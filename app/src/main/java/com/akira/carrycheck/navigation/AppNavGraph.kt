package com.akira.carrycheck.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.akira.carrycheck.ui.screens.main.MainScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(navController = navController)
        }

        // 将来的な画面追加用（Phase3で必要に応じて実装）
        composable("emergency") {
            // EmergencyModeScreen(navController = navController)
        }

        composable("history") {
            // HistoryListScreen(navController = navController)
        }

        composable("customization") {
            // CustomizationScreen(navController = navController)
        }

        composable("voice_settings") {
            // VoiceSettingScreen(navController = navController)
        }
    }
}
