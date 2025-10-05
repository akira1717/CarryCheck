package com.akira.carrycheck.presentation.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import com.akira.carrycheck.data.model.ChecklistItem
import com.akira.carrycheck.data.model.Season
import com.akira.carrycheck.data.repository.ItemRepository
import com.akira.carrycheck.util.TextToSpeechUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import java.util.*

/**
 * CarryCheck v3.0 履歴管理ViewModel
 * リスト保存・再利用・スマート提案機能
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val textToSpeechUtil: TextToSpeechUtil
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private var allHistoryItems = listOf<HistoryItem>()

    init {
        loadHistoryData()
        generateSmartSuggestions()
        updateCurrentSeason()
    }

    /**
     * 履歴データ読み込み
     */
    private fun loadHistoryData() {
        viewModelScope.launch {
            // 実際の履歴データ読み込み（現在はモックデータ）
            // TODO: 実際のデータベースから履歴読み込み実装
            allHistoryItems = generateMockHistoryData()
            applyFilters()
        }
    }

    /**
     * スマート提案生成
     */
    private fun generateSmartSuggestions() {
        viewModelScope.launch {
            val suggestions = mutableListOf<SmartSuggestion>()

            // 現在の季節に応じた提案
            val currentSeason = getCurrentSeason()
            when (currentSeason) {
                Season.SPRING -> {
                    suggestions.add(
                        SmartSuggestion(
                            id = "spring_items",
                            title = "春のお出かけセット",
                            description = "お花見や春のお出かけに必要な持ち物",
                            items = listOf("カメラ", "レジャーシート", "水筒", "日焼け止め"),
                            reason = "春の季節によく使われるアイテムです"
                        )
                    )
                }
                Season.SUMMER -> {
                    suggestions.add(
                        SmartSuggestion(
                            id = "summer_items",
                            title = "夏のお出かけセット",
                            description = "暑い日のお出かけに必要な持ち物",
                            items = listOf("帽子", "日傘", "タオル", "冷感グッズ", "水筒"),
                            reason = "夏の暑さ対策アイテムです"
                        )
                    )
                }
                Season.AUTUMN -> {
                    suggestions.add(
                        SmartSuggestion(
                            id = "autumn_items",
                            title = "秋のお出かけセット",
                            description = "紅葉狩りや秋のお出かけに必要な持ち物",
                            items = listOf("カメラ", "軽い上着", "温かい飲み物", "手袋"),
                            reason = "秋の季節によく使われるアイテムです"
                        )
                    )
                }
                Season.WINTER -> {
                    suggestions.add(
                        SmartSuggestion(
                            id = "winter_items",
                            title = "冬のお出かけセット",
                            description = "寒い日のお出かけに必要な持ち物",
                            items = listOf("手袋", "マフラー", "カイロ", "マスク", "リップクリーム"),
                            reason = "冬の寒さ対策アイテムです"
                        )
                    )
                }
            }

            // よく忘れるアイテムの提案
            suggestions.add(
                SmartSuggestion(
                    id = "frequently_forgotten",
                    title = "よく忘れるアイテム",
                    description = "過去によく忘れていたアイテムのリマインド",
                    items = listOf("鍵", "財布", "スマートフォン", "充電器"),
                    reason = "忘れやすいアイテムをまとめました"
                )
            )

            _uiState.value = _uiState.value.copy(smartSuggestions = suggestions)
        }
    }

    /**
     * 検索クエリ更新
     */
    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    /**
     * 季節フィルター
     */
    fun filterBySeason(season: Season?) {
        _uiState.value = _uiState.value.copy(selectedSeason = season)
        applyFilters()
    }

    /**
     * フィルタークリア
     */
    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            selectedSeason = null
        )
        applyFilters()
    }

    /**
     * タブ選択
     */
    fun selectTab(tab: HistoryTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
        applyFilters()
    }

    /**
     * フィルター適用
     */
    private fun applyFilters() {
        val currentState = _uiState.value
        var filteredItems = allHistoryItems

        // 検索フィルター
        if (currentState.searchQuery.isNotBlank()) {
            filteredItems = filteredItems.filter { historyItem ->
                historyItem.title.contains(currentState.searchQuery, ignoreCase = true) ||
                        historyItem.items.any { it.contains(currentState.searchQuery, ignoreCase = true) }
            }
        }

        // 季節フィルター
        if (currentState.selectedSeason != null) {
            filteredItems = filteredItems.filter { it.season == currentState.selectedSeason }
        }

        // タブフィルター
        filteredItems = when (currentState.selectedTab) {
            HistoryTab.ALL -> filteredItems
            HistoryTab.RECENT -> filteredItems.filter {
                (System.currentTimeMillis() - it.lastUsed) < 7 * 24 * 60 * 60 * 1000L // 7日以内
            }
            HistoryTab.FAVORITES -> filteredItems.filter { it.isFavorite }
            HistoryTab.FREQUENT -> filteredItems.sortedByDescending { it.usageCount }.take(10)
        }

        _uiState.value = currentState.copy(filteredHistoryItems = filteredItems)
    }

    /**
     * 履歴リスト復元
     */
    fun restoreHistoryList(historyItem: HistoryItem) {
        viewModelScope.launch {
            // 履歴アイテムを現在のチェックリストに追加
            historyItem.items.forEach { itemName ->
                val newItem = ChecklistItem(
                    id = 0,
                    name = itemName,
                    isChecked = false,
                    season = getCurrentSeason(),
                    createdAt = System.currentTimeMillis(),
                    isImportant = false
                )
                itemRepository.insertItem(newItem)
            }

            // 使用回数増加
            updateHistoryUsage(historyItem.id)

            textToSpeechUtil.speak("${historyItem.title}を復元しました")
        }
    }

    /**
     * 履歴削除
     */
    fun deleteHistory(historyId: String) {
        allHistoryItems = allHistoryItems.filter { it.id != historyId }
        applyFilters()
        textToSpeechUtil.speak("履歴を削除しました")
    }

    /**
     * お気に入り切り替え
     */
    fun toggleFavorite(historyId: String) {
        allHistoryItems = allHistoryItems.map { historyItem ->
            if (historyItem.id == historyId) {
                historyItem.copy(isFavorite = !historyItem.isFavorite)
            } else {
                historyItem
            }
        }
        applyFilters()
    }

    /**
     * 履歴詳細表示
     */
    fun showHistoryDetails(historyItem: HistoryItem) {
        _uiState.value = _uiState.value.copy(
            selectedHistoryItem = historyItem,
            showHistoryDetails = true
        )
    }

    /**
     * スマート提案適用
     */
    fun applySuggestion(suggestion: SmartSuggestion) {
        viewModelScope.launch {
            suggestion.items.forEach { itemName ->
                val newItem = ChecklistItem(
                    id = 0,
                    name = itemName,
                    isChecked = false,
                    season = getCurrentSeason(),
                    createdAt = System.currentTimeMillis(),
                    isImportant = false
                )
                itemRepository.insertItem(newItem)
            }

            textToSpeechUtil.speak("${suggestion.title}を追加しました")
        }
    }

    /**
     * スマート提案却下
     */
    fun dismissSuggestion(suggestion: SmartSuggestion) {
        val currentSuggestions = _uiState.value.smartSuggestions
        _uiState.value = _uiState.value.copy(
            smartSuggestions = currentSuggestions.filter { it.id != suggestion.id }
        )
    }

    /**
     * 全履歴削除ダイアログ表示
     */
    fun showClearAllDialog() {
        _uiState.value = _uiState.value.copy(showClearAllDialog = true)
    }

    /**
     * 全履歴削除ダイアログ非表示
     */
    fun dismissClearAllDialog() {
        _uiState.value = _uiState.value.copy(showClearAllDialog = false)
    }

    /**
     * 全履歴削除
     */
    fun clearAllHistory() {
        allHistoryItems = emptyList()
        applyFilters()
        textToSpeechUtil.speak("すべての履歴を削除しました")
    }

    /**
     * 履歴使用回数更新
     */
    private fun updateHistoryUsage(historyId: String) {
        allHistoryItems = allHistoryItems.map { historyItem ->
            if (historyItem.id == historyId) {
                historyItem.copy(
                    usageCount = historyItem.usageCount + 1,
                    lastUsed = System.currentTimeMillis()
                )
            } else {
                historyItem
            }
        }
    }

    /**
     * 現在の季節取得
     */
    private fun getCurrentSeason(): Season {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        return when (currentMonth) {
            Calendar.MARCH, Calendar.APRIL, Calendar.MAY -> Season.SPRING
            Calendar.JUNE, Calendar.JULY, Calendar.AUGUST -> Season.SUMMER
            Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER -> Season.AUTUMN
            else -> Season.WINTER
        }
    }

    /**
     * 現在の季節更新
     */
    private fun updateCurrentSeason() {
        _uiState.value = _uiState.value.copy(currentSeason = getCurrentSeason())
    }

    /**
     * モック履歴データ生成
     */
    private fun generateMockHistoryData(): List<HistoryItem> {
        return listOf(
            HistoryItem(
                id = "history_1",
                title = "通勤セット",
                items = listOf("財布", "定期券", "スマートフォン", "鍵", "会社の鍵"),
                season = Season.AUTUMN,
                createdAt = System.currentTimeMillis() - 86400000L, // 1日前
                lastUsed = System.currentTimeMillis() - 3600000L, // 1時間前
                usageCount = 15,
                isFavorite = true
            ),
            HistoryItem(
                id = "history_2",
                title = "お出かけセット",
                items = listOf("財布", "スマートフォン", "鍵", "ハンカチ", "ティッシュ"),
                season = Season.AUTUMN,
                createdAt = System.currentTimeMillis() - 172800000L, // 2日前
                lastUsed = System.currentTimeMillis() - 7200000L, // 2時間前
                usageCount = 8,
                isFavorite = false
            ),
            HistoryItem(
                id = "history_3",
                title = "旅行セット",
                items = listOf("パスポート", "財布", "スマートフォン", "充電器", "着替え", "薬"),
                season = Season.SUMMER,
                createdAt = System.currentTimeMillis() - 604800000L, // 7日前
                lastUsed = System.currentTimeMillis() - 604800000L, // 7日前
                usageCount = 3,
                isFavorite = true
            )
        )
    }
}

/**
 * 履歴画面のUI状態
 */
data class HistoryUiState(
    val filteredHistoryItems: List<HistoryItem> = emptyList(),
    val searchQuery: String = "",
    val selectedSeason: Season? = null,
    val selectedTab: HistoryTab = HistoryTab.ALL,
    val currentSeason: Season = Season.SPRING,
    val smartSuggestions: List<SmartSuggestion> = emptyList(),
    val selectedHistoryItem: HistoryItem? = null,
    val showHistoryDetails: Boolean = false,
    val showClearAllDialog: Boolean = false
)

/**
 * 履歴アイテム
 */
data class HistoryItem(
    val id: String,
    val title: String,
    val items: List<String>,
    val season: Season,
    val createdAt: Long,
    val lastUsed: Long,
    val usageCount: Int,
    val isFavorite: Boolean
)

/**
 * スマート提案
 */
data class SmartSuggestion(
    val id: String,
    val title: String,
    val description: String,
    val items: List<String>,
    val reason: String
)

/**
 * 履歴タブ
 */
enum class HistoryTab(val displayName: String) {
    ALL("すべて"),
    RECENT("最近"),
    FAVORITES("お気に入り"),
    FREQUENT("よく使う")
}
