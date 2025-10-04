package com.akira.carrycheck.presentation.screen.voice

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akira.carrycheck.data.model.SensitivityLevel
import com.akira.carrycheck.data.model.SpeechRateLevel
import com.akira.carrycheck.presentation.components.VoiceInputButton

/**
 * CarryCheck v3.0 音声設定画面
 * 音声認識設定・練習・感度調整機能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSettingScreen(
    onNavigateBack: () -> Unit,
    viewModel: VoiceSettingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // トップアプリバー
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("音声設定", fontWeight = FontWeight.Bold)
                }
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                }
            },
            actions = {
                // 設定リセットボタン
                TextButton(onClick = { viewModel.resetToDefault() }) {
                    Text("リセット")
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 音声テストセクション
            item {
                VoiceTestSection(
                    isListening = uiState.isListening,
                    recognizedText = uiState.recognizedText,
                    onStartVoiceTest = { viewModel.startVoiceTest() },
                    onStopVoiceTest = { viewModel.stopVoiceTest() },
                    onSpeakTest = { viewModel.speakTest() }
                )
            }

            // 音声認識設定セクション
            item {
                VoiceRecognitionSettingsSection(
                    sensitivity = uiState.recognitionSensitivity,
                    onSensitivityChanged = { viewModel.updateRecognitionSensitivity(it) },
                    timeoutDuration = uiState.timeoutDuration,
                    onTimeoutChanged = { viewModel.updateTimeoutDuration(it) },
                    language = uiState.language,
                    onLanguageChanged = { viewModel.updateLanguage(it) }
                )
            }

            // 音声合成設定セクション
            item {
                VoiceSynthesisSettingsSection(
                    speechRate = uiState.speechRate,
                    onSpeechRateChanged = { viewModel.updateSpeechRate(it) },
                    speechPitch = uiState.speechPitch,
                    onSpeechPitchChanged = { viewModel.updateSpeechPitch(it) },
                    isVoiceFeedbackEnabled = uiState.isVoiceFeedbackEnabled,
                    onVoiceFeedbackToggle = { viewModel.toggleVoiceFeedback() }
                )
            }

            // 練習モードセクション
            item {
                VoicePracticeModeSection(
                    isPracticeMode = uiState.practiceMode,
                    onPracticeModeToggle = { viewModel.togglePracticeMode() },
                    practiceScore = uiState.practiceScore,
                    practiceLevel = uiState.practiceLevel,
                    onStartPractice = { viewModel.startPracticeSession() }
                )
            }

            // 緊急モード設定セクション
            item {
                EmergencyModeSettingsSection(
                    isEmergencyModeEnabled = uiState.emergencyModeEnabled,
                    onEmergencyModeToggle = { viewModel.toggleEmergencyMode() },
                    emergencySettings = uiState.emergencyVoiceSettings
                )
            }

            // アクセシビリティ設定セクション
            item {
                VoiceAccessibilitySection(
                    isHighSensitivityMode = uiState.isHighSensitivityMode,
                    onHighSensitivityToggle = { viewModel.toggleHighSensitivityMode() },
                    isSlowSpeechMode = uiState.isSlowSpeechMode,
                    onSlowSpeechToggle = { viewModel.toggleSlowSpeechMode() },
                    isVerboseMode = uiState.isVerboseMode,
                    onVerboseModeToggle = { viewModel.toggleVerboseMode() }
                )
            }
        }
    }
}

/**
 * 音声テストセクション
 */
@Composable
private fun VoiceTestSection(
    isListening: Boolean,
    recognizedText: String,
    onStartVoiceTest: () -> Unit,
    onStopVoiceTest: () -> Unit,
    onSpeakTest: () -> Unit
) {
    VoiceSettingCard(
        title = "音声テスト",
        icon = Icons.Default.Mic,
        description = "音声認識と読み上げをテストします"
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 音声入力テスト
            VoiceInputButton(
                isListening = isListening,
                onStartVoiceInput = onStartVoiceTest,
                onStopVoiceInput = onStopVoiceTest
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 認識結果表示
            if (recognizedText.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = "認識結果: $recognizedText",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // 読み上げテストボタン
            Button(
                onClick = onSpeakTest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("読み上げテスト")
            }
        }
    }
}

/**
 * 音声認識設定セクション
 */
@Composable
private fun VoiceRecognitionSettingsSection(
    sensitivity: Float,
    onSensitivityChanged: (Float) -> Unit,
    timeoutDuration: Long,
    onTimeoutChanged: (Long) -> Unit,
    language: String,
    onLanguageChanged: (String) -> Unit
) {
    VoiceSettingCard(
        title = "音声認識設定",
        icon = Icons.Default.SettingsVoice,
        description = "音声認識の詳細設定"
    ) {
        Column {
            // 認識感度設定
            Text(
                text = "認識感度: ${getSensitivityDisplayText(sensitivity)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Slider(
                value = sensitivity,
                onValueChange = onSensitivityChanged,
                valueRange = 0.1f..1.0f,
                steps = 8,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "低感度: 静かな環境 ← → 高感度: 騒がしい環境",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // タイムアウト設定
            Text(
                text = "認識タイムアウト: ${timeoutDuration / 1000}秒",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Slider(
                value = timeoutDuration.toFloat(),
                onValueChange = { onTimeoutChanged(it.toLong()) },
                valueRange = 3000f..15000f,
                steps = 11,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 言語設定
            Text(
                text = "認識言語",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val languages = listOf(
                "ja-JP" to "日本語",
                "en-US" to "English (US)",
                "en-GB" to "English (UK)"
            )

            languages.forEach { (code, name) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = language == code,
                        onClick = { onLanguageChanged(code) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/**
 * 音声合成設定セクション
 */
@Composable
private fun VoiceSynthesisSettingsSection(
    speechRate: Float,
    onSpeechRateChanged: (Float) -> Unit,
    speechPitch: Float,
    onSpeechPitchChanged: (Float) -> Unit,
    isVoiceFeedbackEnabled: Boolean,
    onVoiceFeedbackToggle: () -> Unit
) {
    VoiceSettingCard(
        title = "音声合成設定",
        icon = Icons.Default.VolumeUp,
        description = "読み上げの詳細設定"
    ) {
        Column {
            // 音声フィードバック有効/無効
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "音声フィードバック",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "操作結果を音声で通知",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Switch(
                    checked = isVoiceFeedbackEnabled,
                    onCheckedChange = { onVoiceFeedbackToggle() }
                )
            }

            if (isVoiceFeedbackEnabled) {
                Spacer(modifier = Modifier.height(16.dp))

                // 読み上げ速度
                Text(
                    text = "読み上げ速度: ${getSpeechRateDisplayText(speechRate)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Slider(
                    value = speechRate,
                    onValueChange = onSpeechRateChanged,
                    valueRange = 0.5f..2.0f,
                    steps = 14,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 読み上げピッチ
                Text(
                    text = "読み上げピッチ: ${getSpeechPitchDisplayText(speechPitch)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Slider(
                    value = speechPitch,
                    onValueChange = onSpeechPitchChanged,
                    valueRange = 0.5f..2.0f,
                    steps = 14,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 音声設定カード共通コンポーネント
 */
@Composable
private fun VoiceSettingCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

// ヘルパー関数
private fun getSensitivityDisplayText(sensitivity: Float): String {
    return when {
        sensitivity <= 0.3f -> "低"
        sensitivity <= 0.7f -> "中"
        else -> "高"
    }
}

private fun getSpeechRateDisplayText(rate: Float): String {
    return when {
        rate <= 0.8f -> "ゆっくり"
        rate <= 1.2f -> "標準"
        else -> "早い"
    }
}

private fun getSpeechPitchDisplayText(pitch: Float): String {
    return when {
        pitch <= 0.8f -> "低い"
        pitch <= 1.2f -> "標準"
        else -> "高い"
    }
}

// 残りのセクション（VoicePracticeMode, EmergencyModeSettings, VoiceAccessibility）は
// VoiceSettingViewModel.ktで定義予定
