package com.akira.carrycheck.data.model

/**
 * 音声設定のデータモデル
 *
 * @property id 設定の一意識別子
 * @property userId ユーザーID（将来の拡張用）
 * @property isVoiceRecognitionEnabled 音声認識機能の有効/無効
 * @property voiceRecognitionLanguage 音声認識の言語設定
 * @property maxRecognitionAttempts 音声認識の最大試行回数
 * @property isTtsEnabled 音声読み上げ機能の有効/無効
 * @property ttsLanguage 音声読み上げの言語設定
 * @property ttsSpeed 音声読み上げの速度（0.5～2.0）
 * @property ttsPitch 音声読み上げのピッチ（0.5～2.0）
 * @property createdAt 作成日時
 * @property updatedAt 更新日時
 */
data class VoiceSetting(
    val id: Long = 0L,
    val userId: String = "default_user",
    val isVoiceRecognitionEnabled: Boolean = true,
    val voiceRecognitionLanguage: String = "ja-JP",
    val maxRecognitionAttempts: Int = 3,
    val isTtsEnabled: Boolean = true,
    val ttsLanguage: String = "ja-JP",
    val ttsSpeed: Float = 1.0f,
    val ttsPitch: Float = 1.0f,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
