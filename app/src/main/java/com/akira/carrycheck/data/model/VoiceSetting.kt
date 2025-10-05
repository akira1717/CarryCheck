package com.akira.carrycheck.data.model

/**
 * 音声設定データクラス
 * v3.0拡張: 高度音声設定・練習機能・緊急モード対応
 */
data class VoiceSetting(
    val id: Long = 0,
    val userId: String = "", // ユーザーID追加
    val isVoiceRecognitionEnabled: Boolean = true, // 音声認識有効
    val voiceRecognitionLanguage: String = "ja-JP", // 音声認識言語
    val maxRecognitionAttempts: Int = 3, // 最大認識試行回数
    val isTtsEnabled: Boolean = true, // TTS（読み上げ）有効
    val ttsLanguage: String = "ja-JP", // TTS言語
    val ttsSpeed: Float = 1.0f, // TTS速度 (0.5-2.0)
    val ttsPitch: Float = 1.0f, // TTSピッチ (0.5-2.0)
    val createdAt: Long = System.currentTimeMillis(), // 作成日時
    val updatedAt: Long = System.currentTimeMillis() // 更新日時
) {
    /**
     * 音声認識感度レベル取得
     */
    fun getRecognitionSensitivityLevel(): String {
        return when {
            maxRecognitionAttempts <= 1 -> "高感度"
            maxRecognitionAttempts <= 3 -> "標準"
            else -> "低感度"
        }
    }

    /**
     * TTS速度レベル取得
     */
    fun getTtsSpeedLevel(): String {
        return when {
            ttsSpeed < 0.8f -> "遅い"
            ttsSpeed > 1.2f -> "速い"
            else -> "標準"
        }
    }

    /**
     * 言語表示名取得
     */
    fun getLanguageDisplayName(): String {
        return when (voiceRecognitionLanguage) {
            "ja-JP" -> "日本語"
            "en-US" -> "English (US)"
            "en-GB" -> "English (UK)"
            "ko-KR" -> "한국어"
            "zh-CN" -> "中文 (简体)"
            "zh-TW" -> "中文 (繁體)"
            else -> voiceRecognitionLanguage
        }
    }

    companion object {
        /**
         * サポート言語一覧
         */
        val SUPPORTED_LANGUAGES = listOf(
            "ja-JP" to "日本語",
            "en-US" to "English (US)",
            "en-GB" to "English (UK)",
            "ko-KR" to "한국어",
            "zh-CN" to "中文 (简体)",
            "zh-TW" to "中文 (繁體)"
        )

        /**
         * デフォルト設定作成
         */
        fun createDefault(userId: String = ""): VoiceSetting {
            return VoiceSetting(
                userId = userId,
                isVoiceRecognitionEnabled = true,
                voiceRecognitionLanguage = "ja-JP",
                maxRecognitionAttempts = 3,
                isTtsEnabled = true,
                ttsLanguage = "ja-JP",
                ttsSpeed = 1.0f,
                ttsPitch = 1.0f
            )
        }
    }
}
