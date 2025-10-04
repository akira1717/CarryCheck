package com.akira.carrycheck.presentation.screen.history

import androidx.compose.animation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akira.carrycheck.data.model.ChecklistItem
import com.akira.carrycheck.data.model.Season
import com.akira.carrycheck.presentation.components.SeasonalBackground

/**
 * CarryCheck v3.0 履歴管理画面
 * リスト保存・再利用・スマート提案機能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryListScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // 季節背景
        SeasonalBackground(season = uiState.currentSeason)

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
                            Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("履歴管理", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                    }
                },
                actions = {
                    // 全履歴削除ボタン
                    IconButton(onClick = { viewModel.showClearAllDialog() }) {
                        Icon(
                            Icons.Default.DeleteSweep,
                            contentDescription = "全削除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 検索・フィルター
                SearchAndFilterSection(
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    selectedSeason = uiState.selectedSeason,
                    onSeasonSelected = { viewModel.filterBySeason(it) },
                    onClearFilters = { viewModel.clearFilters() }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // スマート提案セクション
                if (uiState.smartSuggestions.isNotEmpty()) {
                    SmartSuggestionsSection(
                        suggestions = uiState.smartSuggestions,
                        onSuggestionSelected = { viewModel.applySuggestion(it) },
                        onDismissSuggestion = { viewModel.dismissSuggestion(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 履歴タブ
                HistoryTabs(
                    selectedTab = uiState.selectedTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 履歴リスト
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.filteredHistoryItems) { historyItem ->
                        HistoryItemCard(
                            historyItem = historyItem,
                            onRestoreList = { viewModel.restoreHistoryList(historyItem) },
                            onDeleteHistory = { viewModel.deleteHistory(historyItem.id) },
                            onToggleFavorite = { viewModel.toggleFavorite(historyItem.id) },
                            onViewDetails = { viewModel.showHistoryDetails(historyItem) }
                        )
                    }

                    // 空状態表示
                    if (uiState.filteredHistoryItems.isEmpty()) {
                        item {
                            EmptyHistoryState(
                                message = when {
                                    uiState.searchQuery.isNotBlank() -> "検索結果が見つかりません"
                                    uiState.selectedSeason != null -> "この季節の履歴がありません"
                                    else -> "履歴がありません\n使用していくと履歴が蓄積されます"
                                }
                            )
                        }
                    }
                }
            }
        }

        // 全削除確認ダイアログ
        if (uiState.showClearAllDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissClearAllDialog() },
                title = { Text("全履歴削除") },
                text = { Text("すべての履歴を削除しますか？\nこの操作は取り消せません。") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearAllHistory()
                            viewModel.dismissClearAllDialog()
                        }
                    ) {
                        Text("削除", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissClearAllDialog() }) {
                        Text("キャンセル")
                    }
                }
            )
        }
    }
}

/**
 * 検索・フィルターセクション
 */
@Composable
private fun SearchAndFilterSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedSeason: Season?,
    onSeasonSelected: (Season?) -> Unit,
    onClearFilters: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 検索フィールド
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("履歴を検索") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "クリア")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 季節フィルター
            Text(
                text = "季節で絞り込み",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedSeason == null,
                    onClick = { onSeasonSelected(null) },
                    label = { Text("すべて") }
                )

                Season.values().forEach { season ->
                    FilterChip(
                        selected = selectedSeason == season,
                        onClick = { onSeasonSelected(season) },
                        label = { Text(getSeasonDisplayName(season)) }
                    )
                }
            }

            // フィルタークリア
            if (searchQuery.isNotBlank() || selectedSeason != null) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onClearFilters,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("フィルタークリア")
                }
            }
        }
    }
}

/**
 * スマート提案セクション
 */
@Composable
private fun SmartSuggestionsSection(
    suggestions: List<SmartSuggestion>,
    onSuggestionSelected: (SmartSuggestion) -> Unit,
    onDismissSuggestion: (SmartSuggestion) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "スマート提案",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            suggestions.forEach { suggestion ->
                SuggestionCard(
                    suggestion = suggestion,
                    onSelected = { onSuggestionSelected(suggestion) },
                    onDismiss = { onDismissSuggestion(suggestion) }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

/**
 * 履歴タブ
 */
@Composable
private fun HistoryTabs(
    selectedTab: HistoryTab,
    onTabSelected: (HistoryTab) -> Unit
) {
    TabRow(selectedTabIndex = selectedTab.ordinal) {
        HistoryTab.values().forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = { Text(tab.displayName) }
            )
        }
    }
}

// ヘルパー関数とデータクラス
private fun getSeasonDisplayName(season: Season): String {
    return when (season) {
        Season.SPRING -> "春"
        Season.SUMMER -> "夏"
        Season.AUTUMN -> "秋"
        Season.WINTER -> "冬"
    }
}

// 残りのコンポーネントとデータクラスは次のファイルで定義予定
