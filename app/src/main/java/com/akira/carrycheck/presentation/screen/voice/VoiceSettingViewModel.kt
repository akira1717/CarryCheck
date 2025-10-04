package com.akira.carrycheck.presentation.screen.voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.akira.carrycheck.data.model.VoiceSetting
import com.akira.carrycheck.data.repository.VoiceSettingRepository
import com.akira.carrycheck.utils.VoiceRecognitionUtil
import com.akira.carrycheck.utils.TextToSpeechUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * CarryCheck v3.0 音声設定ViewModel
 * 音声認識設定・練習・感度調整・緊急モード・アクセシビリティ対応
 */
@HiltViewModel
class VoiceSettingViewModel @Inject constructor(
    private val voiceSettingRepository: VoiceSettingRepository,
    private val voiceRecognitionUtil: VoiceRecognitionUtil,
    private val textToSpeechUtil: TextToSpeechUtil
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoiceSettingUiState())
    val uiState: StateFlow<VoiceSettingUiState> = _uiState.asStateFlow()

    init {
        loadVoiceSettings()
        initializeVoiceServices()
    }

    /**
     * 音声設定読み込み
     */
    private fun loadVoiceSettings() {
        viewModelScope.launch {
            voiceSettingRepository.getCurrentVoiceSetting().collect { setting ->
                setting?.let {
                    _uiState.value = _uiState.value.copy(
                        recognitionSensitivity = it.recognitionSensitivity,
                        speechRate = it.speechRate,
                        speechPitch = it.speechPitch,
                        language = it.language,
                        isVoiceFeedbackEnabled = it.isVoiceFeedbackEnabled,
                        timeoutDuration = it.timeoutDuration,
                        practiceMode = it.practiceMode,
                        emergencyModeEnabled = it.emergencyModeEnabled
                    )

                    // 音声サービスに設定を適用
                    applySettingsToVoiceServices(it)
                }
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
     * 音声サービスに設定適用
     */
    private fun applySettingsToVoiceServices(setting: VoiceSetting) {
        textToSpeechUtil.setSpeechRate(setting.speechRate)
        textToSpeechUtil.setSpeechPitch(setting.speechPitch)
        textToSpeechUtil.setLanguage(setting.language)
        voiceRecognitionUtil.setSensitivity(setting.recognitionSensitivity)
        voiceRecognitionUtil.setTimeout(setting.timeoutDuration)
    }

    /**
     * 音声テスト開始
     */
    fun startVoiceTest() {
        _uiState.value = _uiState.value.copy(isListening = true)

        voiceRecognitionUtil.startListening(
            onResult = { recognizedText ->
                _uiState.value = _uiState.value.copy(
                    isListening = false,
                    recognizedText = recognizedText
                )
                textToSpeechUtil.speak("認識しました: $recognizedText")
            },
            onError = { error ->
                _uiState.value = _uiState.value.copy(
                    isListening = false,
                    recognizedText = "認識エラー: $error"
                )
                textToSpeechUtil.speak("音声認識に失敗しました")
            }
        )
    }

    /**
     * 音声テスト停止
     */
    fun stopVoiceTest() {
        _uiState.value = _uiState.value.copy(isListening = false)
        voiceRecognitionUtil.stopListening()
    }

    /**
     * 読み上げテスト
     */
    fun speakTest() {
        val testMessages = listOf(
            "これは音声テストです。",
            "キャリーちゃんと一緒に持ち物をチェックしましょう。",
            "音声認識の設定が完了しました。"
        )
        val randomMessage = testMessages.random()
        textToSpeechUtil.speak(randomMessage)
    }

    /**
     * 音声認識感度更新
     */
    fun updateRecognitionSensitivity(sensitivity: Float) {
        _uiState.value = _uiState.value.copy(recognitionSensitivity = sensitivity)
        voiceRecognitionUtil.setSensitivity(sensitivity)
        saveSettings()

        val level = when {
            sensitivity <= 0.3f -> "低感度"
            sensitivity <= 0.7f -> "中感度"
            else -> "高感度"
        }
        textToSpeechUtil.speak("音声認識を${level}に設定しました")
    }

    /**
     * タイムアウト時間更新
     */
    fun updateTimeoutDuration(duration: Long) {
        _uiState.value = _uiState.value.copy(timeoutDuration = duration)
        voiceRecognitionUtil.setTimeout(duration)
        saveSettings()
    }

    /**
     * 言語更新
     */
    fun updateLanguage(language: String) {
        _uiState.value = _uiState.value.copy(language = language)
        textToSpeechUtil.setLanguage(language)
        saveSettings()

        val languageName = when (language) {
            "ja-JP" -> "日本語"
            "en-US" -> "英語（アメリカ）"
            "en-GB" -> "英語（イギリス）"
            else -> "選択した言語"
        }
        textToSpeechUtil.speak("言語を${languageName}に設定しました")
    }

    /**
     * 読み上げ速度更新
     */
    fun updateSpeechRate(rate: Float) {
        _uiState.value = _uiState.value.copy(speechRate = rate)
        textToSpeechUtil.setSpeechRate(rate)
        saveSettings()
    }

    /**
     * 読み上げピッチ更新
     */
    fun updateSpeechPitch(pitch: Float) {
        _uiState.value = _uiState.value.copy(speechPitch = pitch)
        textToSpeechUtil.setSpeechPitch(pitch)
        saveSettings()
    }

    /**
     * 音声フィードバック切り替え
     */
    fun toggleVoiceFeedback() {
        val newValue = !_uiState.value.isVoiceFeedbackEnabled
        _uiState.value = _uiState.value.copy(isVoiceFeedbackEnabled = newValue)
        saveSettings()

        if (newValue) {
            textToSpeechUtil.speak("音声フィードバックを有効にしました")
        }
    }

    /**
     * 練習モード切り替え
     */
    fun togglePracticeMode() {
        val newValue = !_uiState.value.practiceMode
        _uiState.value = _uiState.value.copy(practiceMode = newValue)
        saveSettings()

        textToSpeechUtil.speak(if (newValue) "練習モードを開始します" else "練習モードを終了します")
    }

    /**
     * 練習セッション開始
     */
    fun startPracticeSession() {
        _uiState.value = _uiState.value.copy(
            practiceMode = true,
            practiceScore = 0,
            practiceLevel = PracticeLevel.BEGINNER
        )

        textToSpeechUtil.speak("音声認識の練習を開始します。次の言葉を言ってください：財布")

        // 練習用音声認識開始
        voiceRecognitionUtil.startListening(
            onResult = { result ->
                handlePracticeResult(result, "財布")
            },
            onError = { error ->
                textToSpeechUtil.speak("もう一度試してください")
            }
        )
    }

    /**
     * 練習結果処理
     */
    private fun handlePracticeResult(result: String, expected: String) {
        val isCorrect = result.contains(expected, ignoreCase = true)
        val currentScore = _uiState.value.practiceScore
        val newScore = if (isCorrect) currentScore + 10 else currentScore

        _uiState.value = _uiState.value.copy(practiceScore = newScore)

        if (isCorrect) {
            textToSpeechUtil.speak("正解です！スコア：$newScore")
        } else {
            textToSpeechUtil.speak("「$expected」と言ってください。認識結果：$result")
        }
    }

    /**
     * 緊急モード切り替え
     */
    fun toggleEmergencyMode() {
        val newValue = !_uiState.value.emergencyModeEnabled
        _uiState.value = _uiState.value.copy(emergencyModeEnabled = newValue)

        if (newValue) {
            // 緊急モード用設定を適用
            _uiState.value = _uiState.value.copy(
                emergencyVoiceSettings = EmergencyVoiceSettings(
                    highSensitivity = true,
                    shortTimeout = true,
                    loudFeedback = true,
                    simplifiedCommands = true
                )
            )
        }

        saveSettings()
        textToSpeechUtil.speak(if (newValue) "緊急モード対応を有効にしました" else "緊急モード対応を無効にしました")
    }

    /**
     * 高感度モード切り替え（アクセシビリティ）
     */
    fun toggleHighSensitivityMode() {
        val newValue = !_uiState.value.isHighSensitivityMode
        _uiState.value = _uiState.value.copy(isHighSensitivityMode = newValue)

        if (newValue) {
            updateRecognitionSensitivity(0.9f)
        }

        saveSettings()
    }

    /**
     * ゆっくり読み上げモード切り替え（アクセシビリティ）
     */
    fun toggleSlowSpeechMode() {
        val newValue = !_uiState.value.isSlowSpeechMode
        _uiState.value = _uiState.value.copy(isSlowSpeechMode = newValue)

        if (newValue) {
            updateSpeechRate(0.7f)
        } else {
            updateSpeechRate(1.0f)
        }

        saveSettings()
    }

    /**
     * 詳細モード切り替え（アクセシビリティ）
     */
    fun toggleVerboseMode() {
        val newValue = !_uiState.value.isVerboseMode
        _uiState.value = _uiState.value.copy(isVerboseMode = newValue)
        saveSettings()

        textToSpeechUtil.speak(if (newValue) "詳細な音声案内を有効にしました" else "簡潔な音声案内に変更しました")
    }

    /**
     * デフォルト設定にリセット
     */
    fun resetToDefault() {
        viewModelScope.launch {
            val defaultSetting = VoiceSetting()
            voiceSettingRepository.insertVoiceSetting(defaultSetting)

            _uiState.value = VoiceSettingUiState(
                recognitionSensitivity = defaultSetting.recognitionSensitivity,
                speechRate = defaultSetting.speechRate,
                speechPitch = defaultSetting.speechPitch,
                language = defaultSetting.language,
                isVoiceFeedbackEnabled = defaultSetting.isVoiceFeedbackEnabled,
                timeoutDuration = defaultSetting.timeoutDuration,
                practiceMode = defaultSetting.practiceMode,
                emergencyModeEnabled = defaultSetting.emergencyModeEnabled
            )

            applySettingsToVoiceServices(defaultSetting)
            textToSpeechUtil.speak("音声設定をデフォルトに戻しました")
        }
    }

    /**
     * 設定保存
     */
    private fun saveSettings() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val voiceSetting = VoiceSetting(
                recognitionSensitivity = currentState.recognitionSensitivity,
                speechRate = currentState.speechRate,
                speechPitch = currentState.speechPitch,
                language = currentState.language,
                isVoiceFeedbackEnabled = currentState.isVoiceFeedbackEnabled,
                timeoutDuration = currentState.timeoutDuration,
                practiceMode = currentState.practiceMode,
                emergencyModeEnabled = currentState.emergencyModeEnabled
            )

            voiceSettingRepository.insertVoiceSetting(voiceSetting)
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceRecognitionUtil.cleanup()
        textToSpeechUtil.cleanup()
    }
}

/**
 * 音声設定画面のUI状態
 */
data class VoiceSettingUiState(
    // 音声認識設定
    val recognitionSensitivity: Float = 0.7f,
    val timeoutDuration: Long = 5000L,
    val language: String = "ja-JP",

    // 音声合成設定
    val speechRate: Float = 1.0f,
    val speechPitch: Float = 1.0f,
    val isVoiceFeedbackEnabled: Boolean = true,

    // 練習モード
    val practiceMode: Boolean = false,
    val practiceScore: Int = 0,
    val practiceLevel: PracticeLevel = PracticeLevel.BEGINNER,

    // 緊急モード設定
    val emergencyModeEnabled: Boolean = true,
    val emergencyVoiceSettings: EmergencyVoiceSettings = EmergencyVoiceSettings(),

    // アクセシビリティ設定
    val isHighSensitivityMode: Boolean = false,
    val isSlowSpeechMode: Boolean = false,
    val isVerboseMode: Boolean = false,

    // 音声テスト状態
    val isListening: Boolean = false,
    val recognizedText: String = ""
)

/**
 * 練習レベル
 */
enum class PracticeLevel(val displayName: String, val description: String) {
    BEGINNER("初級", "基本的な単語の練習"),
    INTERMEDIATE("中級", "短い文章の練習"),
    ADVANCED("上級", "複雑な文章の練習")
}

/**
 * 緊急モード音声設定
 */
data class EmergencyVoiceSettings(
    val highSensitivity: Boolean = false,
    val shortTimeout: Boolean = false,
    val loudFeedback: Boolean = false,
    val simplifiedCommands: Boolean = false
)
