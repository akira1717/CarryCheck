package com.akira.carrycheck.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.akira.carrycheck.presentation.screen.main.MainScreen
import com.akira.carrycheck.presentation.screen.emergency.EmergencyModeScreen
import com.akira.carrycheck.presentation.screen.history.HistoryListScreen
import com.akira.carrycheck.presentation.screen.customization.CustomizationScreen
import com.akira.carrycheck.presentation.screen.voice.VoiceSettingScreen

/**
 * CarryCheck v3.0 メイン画面遷移設定
 * Phase2実装: 拡張機能への画面遷移
 */
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        // メイン画面（リスト表示・音声入力・チェック機能）
        composable("main") {
            MainScreen(
                onNavigateToEmergency = { navController.navigate("emergency") },
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToCustomization = { navController.navigate("customization") },
                onNavigateToVoiceSetting = { navController.navigate("voice_setting") }
            )
        }

        // 緊急モード画面（クイック追加・タイマー・重要項目表示）
        composable("emergency") {
            EmergencyModeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 履歴管理画面（リスト保存・再利用・スマート提案）
        composable("history") {
            HistoryListScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // カスタマイズ画面（背景・言語・文字サイズ・キャラクター設定）
        composable("customization") {
            CustomizationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 音声設定画面（音声認識設定・練習・感度調整）
        composable("voice_setting") {
            VoiceSettingScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * 画面遷移用のルート定義
 */
object NavigationRoutes {
    const val MAIN = "main"
    const val EMERGENCY = "emergency"
    const val HISTORY = "history"
    const val CUSTOMIZATION = "customization"
    const val VOICE_SETTING = "voice_setting"
}
