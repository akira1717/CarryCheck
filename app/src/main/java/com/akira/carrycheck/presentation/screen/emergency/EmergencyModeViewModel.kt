package com.akira.carrycheck.presentation.screen.emergency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
 * CarryCheck v3.0 緊急モードViewModel
 * タイマー機能・音声強化・重要アイテム管理
 */
@HiltViewModel
class EmergencyModeViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val voiceRecognitionUtil: VoiceRecognitionUtil,
    private val textToSpeechUtil: TextToSpeechUtil
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmergencyModeUiState())
    val uiState: StateFlow<EmergencyModeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var voiceFailureCount = 0
    private val maxVoiceFailures = 2 // 緊急モードでは2回まで

    init {
        loadEmergencyItems()
        initializeVoiceServices()
        setupEmergencyMode()
    }

    /**
     * 緊急モード専用初期化
     */
    private fun setupEmergencyMode() {
        // 緊急モード開始アナウンス
        textToSpeechUtil.speak("緊急モードを開始します。大きな声でハッキリと話してください。")

        // デフォルトタイマー設定（10分）
        _uiState.value = _uiState.value.copy(
            timeRemaining = 10 * 60 * 1000L, // 10分
            emergencyLevel = EmergencyLevel.NORMAL
        )
    }

    /**
     * 緊急アイテム読み込み
     */
    private fun loadEmergencyItems() {
        viewModelScope.launch {
            itemRepository.getAllItems().collect { allItems ->
                // 重要マークされたアイテムまたは最近追加されたアイテム
                val emergencyItems = allItems.filter { item ->
                    item.isImportant ||
                            (System.currentTimeMillis() - item.createdAt) < 24 * 60 * 60 * 1000L // 24時間以内
                }.sortedByDescending { it.createdAt }

                _uiState.value = _uiState.value.copy(emergencyItems = emergencyItems)
            }
        }
    }

    /**
     * 音声サービス初期化（緊急モード設定）
     */
    private fun initializeVoiceServices() {
        voiceRecognitionUtil.initialize()
        textToSpeechUtil.initialize()

        // 緊急モード用音声設定（高感度・短時間タイムアウト）
        voiceRecognitionUtil.setEmergencyMode(true)
    }

    /**
     * タイマー開始
     */
    fun startTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isTimerActive = true)

        timerJob = viewModelScope.launch {
            while (_uiState.value.timeRemaining > 0 && _uiState.value.isTimerActive) {
                delay(1000L)
                val newTime = _uiState.value.timeRemaining - 1000L

                // 緊急レベル更新
                val emergencyLevel = when {
                    newTime <= 60000L -> EmergencyLevel.CRITICAL // 1分以下
                    newTime <= 300000L -> EmergencyLevel.URGENT  // 5分以下
                    else -> EmergencyLevel.NORMAL
                }

                _uiState.value = _uiState.value.copy(
                    timeRemaining = newTime,
                    emergencyLevel = emergencyLevel
                )

                // 緊急アラート
                when (newTime) {
                    300000L -> textToSpeechUtil.speak("あと5分です！") // 5分前
                    180000L -> textToSpeechUtil.speak("あと3分です！急いでください！") // 3分前
                    60000L -> textToSpeechUtil.speak("あと1分です！最終チェック！") // 1分前
                    30000L -> textToSpeechUtil.speak("あと30秒！") // 30秒前
                    0L -> {
                        textToSpeechUtil.speak("時間です！出発してください！")
                        _uiState.value = _uiState.value.copy(isTimerActive = false)
                    }
                }
            }
        }
    }

    /**
     * タイマー停止
     */
    fun stopTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isTimerActive = false)
        textToSpeechUtil.speak("タイマーを停止しました")
    }

    /**
     * タイマーリセット
     */
    fun resetTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            timeRemaining = 10 * 60 * 1000L, // 10分にリセット
            isTimerActive = false,
            emergencyLevel = EmergencyLevel.NORMAL
        )
        textToSpeechUtil.speak("タイマーをリセットしました")
    }

    /**
     * 緊急モード音声入力開始
     */
    fun startVoiceInput() {
        _uiState.value = _uiState.value.copy(isListening = true)

        voiceRecognitionUtil.startListening(
            onResult = { recognizedText ->
                handleEmergencyVoiceResult(recognizedText)
            },
            onError = { error ->
                handleEmergencyVoiceError(error)
            }
        )

        // 緊急モード専用案内
        textToSpeechUtil.speak("大きな声で持ち物を言ってください！")
    }

    /**
     * 音声入力停止
     */
    fun stopVoiceInput() {
        _uiState.value = _uiState.value.copy(isListening = false)
        voiceRecognitionUtil.stopListening()
    }

    /**
     * 緊急モード音声認識結果処理
     */
    private fun handleEmergencyVoiceResult(recognizedText: String) {
        _uiState.value = _uiState.value.copy(isListening = false)

        if (recognizedText.isNotBlank()) {
            val itemName = recognizedText.trim()
            quickAddItem(itemName)
            voiceFailureCount = 0

            // 緊急モード専用フィードバック
            textToSpeechUtil.speak("${itemName}を追加！他にありますか？")
        } else {
            handleEmergencyVoiceError("認識できませんでした")
        }
    }

    /**
     * 緊急モード音声認識エラー処理
     */
    private fun handleEmergencyVoiceError(error: String) {
        _uiState.value = _uiState.value.copy(isListening = false)
        voiceFailureCount++

        if (voiceFailureCount >= maxVoiceFailures) {
            textToSpeechUtil.speak("音声認識できません。クイックボタンを使ってください！")
            _uiState.value = _uiState.value.copy(
                errorMessage = "クイックボタンをご利用ください"
            )
        } else {
            textToSpeechUtil.speak("もう一度、大きな声で！")
        }
    }

    /**
     * クイック追加
     */
    fun quickAddItem(itemName: String) {
        viewModelScope.launch {
            val newItem = ChecklistItem(
                id = 0,
                name = itemName,
                isChecked = false,
                season = getCurrentSeason(),
                createdAt = System.currentTimeMillis(),
                isImportant = true // 緊急モードで追加されたアイテムは重要マーク
            )
            itemRepository.insertItem(newItem)

            textToSpeechUtil.speak("${itemName}を重要アイテムとして追加しました！")
        }
    }

    /**
     * アイテムチェック状態切り替え
     */
    fun toggleItemCheck(itemId: Long) {
        viewModelScope.launch {
            val currentItems = _uiState.value.emergencyItems
            val targetItem = currentItems.find { it.id == itemId }
            targetItem?.let { item ->
                val updatedItem = item.copy(isChecked = !item.isChecked)
                itemRepository.updateItem(updatedItem)

                // 緊急モード専用フィードバック
                val message = if (updatedItem.isChecked) {
                    "${item.name}確認！"
                } else {
                    "${item.name}未確認"
                }
                textToSpeechUtil.speak(message)
            }
        }
    }

    /**
     * 重要マーク設定
     */
    fun markAsImportant(itemId: Long) {
        viewModelScope.launch {
            val currentItems = _uiState.value.emergencyItems
            val targetItem = currentItems.find { it.id == itemId }
            targetItem?.let { item ->
                val updatedItem = item.copy(isImportant = true)
                itemRepository.updateItem(updatedItem)
                textToSpeechUtil.speak("${item.name}を重要アイテムにしました！")
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

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        voiceRecognitionUtil.cleanup()
        textToSpeechUtil.cleanup()
    }
}

/**
 * 緊急モードUI状態
 */
data class EmergencyModeUiState(
    val emergencyItems: List<ChecklistItem> = emptyList(),
    val isListening: Boolean = false,
    val timeRemaining: Long = 10 * 60 * 1000L, // 10分
    val isTimerActive: Boolean = false,
    val emergencyLevel: EmergencyLevel = EmergencyLevel.NORMAL,
    val errorMessage: String? = null
)

/**
 * 緊急レベル
 */
enum class EmergencyLevel {
    NORMAL,   // 通常（10分以上）
    URGENT,   // 急ぎ（5分以下）
    CRITICAL  // 危険（1分以下）
}
