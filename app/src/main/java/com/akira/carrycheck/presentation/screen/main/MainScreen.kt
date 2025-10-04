package com.akira.carrycheck.presentation.screen.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akira.carrycheck.R
import com.akira.carrycheck.data.model.ChecklistItem
import com.akira.carrycheck.presentation.components.VoiceInputButton
import com.akira.carrycheck.presentation.components.ChecklistItemCard
import com.akira.carrycheck.presentation.components.SeasonalBackground

/**
 * CarryCheck v3.0 メイン画面
 * 音声ファースト設計: リスト表示・音声入力・チェック機能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToEmergency: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToCustomization: () -> Unit,
    onNavigateToVoiceSetting: () -> Unit,
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // トップアプリバー
        TopAppBar(
            title = { Text("CarryCheck") },
            actions = {
                // 緊急モードボタン
                IconButton(onClick = onNavigateToEmergency) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "緊急モード",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                // 設定メニューボタン
                IconButton(onClick = onNavigateToCustomization) {
                    Icon(Icons.Default.Settings, contentDescription = "設定")
                }
            }
        )

        // 季節背景 + キャリーちゃん
        Box(modifier = Modifier.fillMaxSize()) {
            SeasonalBackground(season = uiState.currentSeason)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 音声入力セクション
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "持ち物を追加",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // 音声入力ボタン
                        VoiceInputButton(
                            isListening = uiState.isListening,
                            onStartVoiceInput = { viewModel.startVoiceInput() },
                            onStopVoiceInput = { viewModel.stopVoiceInput() }
                        )

                        // 手入力フィールド（音声認識失敗時）
                        if (uiState.showManualInput) {
                            OutlinedTextField(
                                value = uiState.manualInputText,
                                onValueChange = { viewModel.updateManualInput(it) },
                                label = { Text("持ち物を入力") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                trailingIcon = {
                                    IconButton(
                                        onClick = { viewModel.addItemFromManualInput() }
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "追加")
                                    }
                                }
                            )
                        }
                    }
                }

                // 操作ボタン行
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onNavigateToHistory,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.History, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("履歴")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onNavigateToVoiceSetting,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("音声設定")
                    }
                }

                // チェックリスト
                LazyColumn {
                    items(uiState.checklistItems) { item ->
                        ChecklistItemCard(
                            item = item,
                            onCheckedChange = { viewModel.toggleItemCheck(item.id) },
                            onDeleteItem = { viewModel.deleteItem(item.id) },
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
