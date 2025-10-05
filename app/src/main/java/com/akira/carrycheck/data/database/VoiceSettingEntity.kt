package com.akira.carrycheck.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akira.carrycheck.data.model.VoiceSetting

/**
 * 音声設定のRoomエンティティ
 * v3.0拡張: 高度音声設定対応
 */
@Entity(tableName = "voice_settings")
data class VoiceSettingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "", // ユーザーID
    val isVoiceRecognitionEnabled: Boolean = true, // 音声認識有効
    val voiceRecognitionLanguage: String = "ja-JP", // 音声認識言語
    val maxRecognitionAttempts: Int = 3, // 最大認識試行回数
    val isTtsEnabled: Boolean = true, // TTS（読み上げ）有効
    val ttsLanguage: String = "ja-JP", // TTS言語
    val ttsSpeed: Float = 1.0f, // TTS速度 (0.5-2.0)
    val ttsPitch: Float = 1.0f, // TTSピッチ (0.5-2.0)
    val createdAt: Long = System.currentTimeMillis(), // 作成日時
    val updatedAt: Long = System.currentTimeMillis() // 更新日時
)

/**
 * EntityからDomainモデルへの変換
 */
fun VoiceSettingEntity.toDomain(): VoiceSetting {
    return VoiceSetting(
        id = id,
        userId = userId,
        isVoiceRecognitionEnabled = isVoiceRecognitionEnabled,
        voiceRecognitionLanguage = voiceRecognitionLanguage,
        maxRecognitionAttempts = maxRecognitionAttempts,
        isTtsEnabled = isTtsEnabled,
        ttsLanguage = ttsLanguage,
        ttsSpeed = ttsSpeed,
        ttsPitch = ttsPitch,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * DomainモデルからEntityへの変換
 */
fun VoiceSetting.toEntity(): VoiceSettingEntity {
    return VoiceSettingEntity(
        id = id,
        userId = userId,
        isVoiceRecognitionEnabled = isVoiceRecognitionEnabled,
        voiceRecognitionLanguage = voiceRecognitionLanguage,
        maxRecognitionAttempts = maxRecognitionAttempts,
        isTtsEnabled = isTtsEnabled,
        ttsLanguage = ttsLanguage,
        ttsSpeed = ttsSpeed,
        ttsPitch = ttsPitch,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
