package com.akira.carrycheck.data.model

/**
 * 音声設定データクラス
 * v3.0拡張: 高度音声設定・練習機能・緊急モード対応
 */
data class VoiceSetting(
    val id: Long = 0,
    val recognitionSensitivity: Float = 0.7f,    // 音声認識感度 (0.0-1.0)
    val speechRate: Float = 1.0f,                // 読み上げ速度 (0.5-2.0)
    val speechPitch: Float = 1.0f,               // 読み上げピッチ (0.5-2.0)
    val language: String = "ja-JP",              // 言語設定
    val isVoiceFeedbackEnabled: Boolean = true,  // 音声フィードバック有効
    val timeoutDuration: Long = 5000L,           // 音声認識タイムアウト (ms)
    val practiceMode: Boolean = false,           // 練習モード
    val emergencyModeEnabled: Boolean = true     // 緊急モード対応
) {
    /**
     * 音声認識感度レベル取得
     */
    fun getSensitivityLevel(): SensitivityLevel {
        return when {
            recognitionSensitivity <= 0.3f -> SensitivityLevel.LOW
            recognitionSensitivity <= 0.7f -> SensitivityLevel.MEDIUM
            else -> SensitivityLevel.HIGH
        }
    }

    /**
     * 読み上げ速度レベル取得
     */
    fun getSpeechRateLevel(): SpeechRateLevel {
        return when {
            speechRate <= 0.8f -> SpeechRateLevel.SLOW
            speechRate <= 1.2f -> SpeechRateLevel.NORMAL
            else -> SpeechRateLevel.FAST
        }
    }

    /**
     * 言語表示名取得
     */
    fun getLanguageDisplayName(): String {
        return when (language) {
            "ja-JP" -> "日本語"
            "en-US" -> "English (US)"
            "en-GB" -> "English (UK)"
            "ko-KR" -> "한국어"
            "zh-CN" -> "中文 (简体)"
            "zh-TW" -> "中文 (繁體)"
            else -> language
        }
    }

    /**
     * タイムアウト時間（秒）取得
     */
    fun getTimeoutSeconds(): Int {
        return (timeoutDuration / 1000).toInt()
    }

    /**
     * 設定が標準的かどうか判定
     */
    fun isDefaultSettings(): Boolean {
        return recognitionSensitivity == 0.7f &&
                speechRate == 1.0f &&
                speechPitch == 1.0f &&
                language == "ja-JP" &&
                isVoiceFeedbackEnabled &&
                timeoutDuration == 5000L &&
                !practiceMode &&
                emergencyModeEnabled
    }

    /**
     * アクセシビリティ対応設定かどうか判定
     */
    fun isAccessibilityOptimized(): Boolean {
        return speechRate <= 0.8f && // ゆっくり読み上げ
                recognitionSensitivity >= 0.8f && // 高感度
                isVoiceFeedbackEnabled && // フィードバック有効
                timeoutDuration >= 8000L // 長いタイムアウト
    }
}

/**
 * 音声認識感度レベル
 */
enum class SensitivityLevel(val displayName: String, val description: String) {
    LOW("低", "静かな環境向け"),
    MEDIUM("中", "一般的な環境向け"),
    HIGH("高", "騒がしい環境向け")
}

/**
 * 読み上げ速度レベル
 */
enum class SpeechRateLevel(val displayName: String, val description: String) {
    SLOW("ゆっくり", "聞き取りやすい速度"),
    NORMAL("標準", "一般的な読み上げ速度"),
    FAST("早い", "効率的な読み上げ速度")
}

/**
 * サポート言語一覧
 */
object SupportedLanguages {
    val languages = listOf(
        "ja-JP" to "日本語",
        "en-US" to "English (US)",
        "en-GB" to "English (UK)",
        "ko-KR" to "한국어",
        "zh-CN" to "中文 (简体)",
        "zh-TW" to "中文 (繁體)"
    )

    fun getLanguageCode(displayName: String): String? {
        return languages.find { it.second == displayName }?.first
    }

    fun getDisplayName(languageCode: String): String {
        return languages.find { it.first == languageCode }?.second ?: languageCode
    }
}
