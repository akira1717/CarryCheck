package com.akira.carrycheck.presentation.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.akira.carrycheck.data.model.ChecklistItem
import com.akira.carrycheck.data.model.Season
import com.akira.carrycheck.data.repository.ItemRepository
import com.akira.carrycheck.utils.VoiceRecognitionUtil
import com.akira.carrycheck.utils.TextToSpeechUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import java.util.*

/**
 * CarryCheck v3.0 メイン画面ViewModel
 * 音声ファースト設計: 段階式音声入力システム
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val voiceRecognitionUtil: VoiceRecognitionUtil,
    private val textToSpeechUtil: TextToSpeechUtil
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var voiceFailureCount = 0
    private val maxVoiceFailures = 3

    init {
        loadChecklistItems()
        initializeVoiceServices()
        updateSeasonalBackground()
    }

    /**
     * チェックリストアイテム読み込み
     */
    private fun loadChecklistItems() {
        viewModelScope.launch {
            itemRepository.getAllItems().collect { items ->
                _uiState.value = _uiState.value.copy(checklistItems = items)
            }
        }
    }

    /**
     * 音声サービス初期化
     */
    private fun initializeVoiceServices() {
        voiceRecognitionUtil.initialize()
        textToSpeechUtil.initialize()
    }

    /**
     * 季節背景更新
     */
    private fun updateSeasonalBackground() {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val season = when (currentMonth) {
            Calendar.MARCH, Calendar.APRIL, Calendar.MAY -> Season.SPRING
            Calendar.JUNE, Calendar.JULY, Calendar.AUGUST -> Season.SUMMER
            Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER -> Season.AUTUMN
            else -> Season.WINTER
        }
        _uiState.value = _uiState.value.copy(currentSeason = season)
    }

    /**
     * 音声入力開始
     */
    fun startVoiceInput() {
        _uiState.value = _uiState.value.copy(isListening = true)

        voiceRecognitionUtil.startListening(
            onResult = { recognizedText ->
                handleVoiceRecognitionResult(recognizedText)
            },
            onError = { error ->
                handleVoiceRecognitionError(error)
            }
        )

        // 音声入力開始の音声フィードバック
        textToSpeechUtil.speak("持ち物を話してください")
    }

    /**
     * 音声入力停止
     */
    fun stopVoiceInput() {
        _uiState.value = _uiState.value.copy(isListening = false)
        voiceRecognitionUtil.stopListening()
    }

    /**
     * 音声認識結果処理
     */
    private fun handleVoiceRecognitionResult(recognizedText: String) {
        _uiState.value = _uiState.value.copy(isListening = false)

        if (recognizedText.isNotBlank()) {
            addItem(recognizedText.trim())
            voiceFailureCount = 0 // 成功時はカウントリセット
            textToSpeechUtil.speak("${recognizedText}を追加しました")
        } else {
            handleVoiceRecognitionError("認識結果が空です")
        }
    }

    /**
     * 音声認識エラー処理（段階式失敗対応）
     */
    private fun handleVoiceRecognitionError(error: String) {
        _uiState.value = _uiState.value.copy(isListening = false)
        voiceFailureCount++

        if (voiceFailureCount >= maxVoiceFailures) {
            // 3回失敗で手入力モードに切り替え
            _uiState.value = _uiState.value.copy(
                showManualInput = true,
                errorMessage = "音声認識を3回失敗しました。手入力モードに切り替えます。"
            )
            textToSpeechUtil.speak("手入力モードに切り替えます")
        } else {
            val remainingAttempts = maxVoiceFailures - voiceFailureCount
            _uiState.value = _uiState.value.copy(
                errorMessage = "音声認識に失敗しました。あと${remainingAttempts}回試せます。"
            )
            textToSpeechUtil.speak("もう一度話してください")
        }
    }

    /**
     * 手入力テキスト更新
     */
    fun updateManualInput(text: String) {
        _uiState.value = _uiState.value.copy(manualInputText = text)
    }

    /**
     * 手入力からアイテム追加
     */
    fun addItemFromManualInput() {
        val text = _uiState.value.manualInputText.trim()
        if (text.isNotBlank()) {
            addItem(text)
            _uiState.value = _uiState.value.copy(
                manualInputText = "",
                showManualInput = false
            )
            voiceFailureCount = 0 // 手入力成功でカウントリセット
            textToSpeechUtil.speak("${text}を追加しました")
        }
    }

    /**
     * アイテム追加
     */
    private fun addItem(itemName: String) {
        viewModelScope.launch {
            val newItem = ChecklistItem(
                id = 0, // Room の autoGenerate で自動生成
                name = itemName,
                isChecked = false,
                season = _uiState.value.currentSeason,
                createdAt = System.currentTimeMillis()
            )
            itemRepository.insertItem(newItem)
        }
    }

    /**
     * アイテムチェック状態切り替え
     */
    fun toggleItemCheck(itemId: Long) {
        viewModelScope.launch {
            val currentItems = _uiState.value.checklistItems
            val targetItem = currentItems.find { it.id == itemId }
            targetItem?.let { item ->
                val updatedItem = item.copy(isChecked = !item.isChecked)
                itemRepository.updateItem(updatedItem)

                // 音声フィードバック
                val message = if (updatedItem.isChecked) {
                    "${item.name}をチェックしました"
                } else {
                    "${item.name}のチェックを外しました"
                }
                textToSpeechUtil.speak(message)
            }
        }
    }

    /**
     * アイテム削除
     */
    fun deleteItem(itemId: Long) {
        viewModelScope.launch {
            val currentItems = _uiState.value.checklistItems
            val targetItem = currentItems.find { it.id == itemId }
            targetItem?.let { item ->
                itemRepository.deleteItem(item)
                textToSpeechUtil.speak("${item.name}を削除しました")
            }
        }
    }

    /**
     * エラーメッセージクリア
     */
    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    override fun onCleared() {
        super.onCleared()
        voiceRecognitionUtil.cleanup()
        textToSpeechUtil.cleanup()
    }
}

/**
 * メイン画面のUI状態
 */
data class MainUiState(
    val checklistItems: List<ChecklistItem> = emptyList(),
    val isListening: Boolean = false,
    val showManualInput: Boolean = false,
    val manualInputText: String = "",
    val currentSeason: Season = Season.SPRING,
    val errorMessage: String? = null
)
