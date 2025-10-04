package com.akira.carrycheck.presentation.screen.emergency

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akira.carrycheck.data.model.ChecklistItem
import com.akira.carrycheck.presentation.components.VoiceInputButton
import com.akira.carrycheck.presentation.components.ChecklistItemCard

/**
 * CarryCheck v3.0 緊急モード画面
 * クイック追加・タイマー・重要項目表示・アラート強化
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyModeScreen(
    onNavigateBack: () -> Unit,
    viewModel: EmergencyModeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f))
    ) {
        // 緊急モード専用トップバー
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "緊急モード",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // タイマー表示
            EmergencyTimer(
                timeRemaining = uiState.timeRemaining,
                isTimerActive = uiState.isTimerActive,
                onStartTimer = { viewModel.startTimer() },
                onStopTimer = { viewModel.stopTimer() },
                onResetTimer = { viewModel.resetTimer() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 緊急モード専用音声入力
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "急いで追加！",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "大きな声でハッキリと！",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    VoiceInputButton(
                        isListening = uiState.isListening,
                        onStartVoiceInput = { viewModel.startVoiceInput() },
                        onStopVoiceInput = { viewModel.stopVoiceInput() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // クイックアクセスボタン
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickAddButton(
                    text = "財布",
                    onClick = { viewModel.quickAddItem("財布") },
                    modifier = Modifier.weight(1f)
                )
                QuickAddButton(
                    text = "鍵",
                    onClick = { viewModel.quickAddItem("鍵") },
                    modifier = Modifier.weight(1f)
                )
                QuickAddButton(
                    text = "スマホ",
                    onClick = { viewModel.quickAddItem("スマートフォン") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 重要項目リスト
            Text(
                text = "緊急チェックリスト",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(uiState.emergencyItems) { item ->
                    EmergencyItemCard(
                        item = item,
                        onCheckedChange = { viewModel.toggleItemCheck(item.id) },
                        onMakeImportant = { viewModel.markAsImportant(item.id) },
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * 緊急モードタイマー
 */
@Composable
private fun EmergencyTimer(
    timeRemaining: Long,
    isTimerActive: Boolean,
    onStartTimer: () -> Unit,
    onStopTimer: () -> Unit,
    onResetTimer: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (timeRemaining < 60000) { // 1分未満
                MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "出発まで",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = formatTime(timeRemaining),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = if (timeRemaining < 60000) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = if (isTimerActive) onStopTimer else onStartTimer
                ) {
                    Icon(
                        if (isTimerActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isTimerActive) "停止" else "開始")
                }

                OutlinedButton(onClick = onResetTimer) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("リセット")
                }
            }
        }
    }
}

/**
 * クイック追加ボタン
 */
@Composable
private fun QuickAddButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        )
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 緊急モード専用アイテムカード
 */
@Composable
private fun EmergencyItemCard(
    item: ChecklistItem,
    onCheckedChange: (Boolean) -> Unit,
    onMakeImportant: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isImportant) {
                MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (item.isImportant) 6.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.error
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = item.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (item.isImportant) FontWeight.Bold else FontWeight.Normal
            )

            if (item.isImportant) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "重要",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                IconButton(
                    onClick = onMakeImportant,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.StarBorder,
                        contentDescription = "重要マーク",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * 時間フォーマット
 */
private fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / (1000 * 60)) % 60
    val hours = (milliseconds / (1000 * 60 * 60))

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
