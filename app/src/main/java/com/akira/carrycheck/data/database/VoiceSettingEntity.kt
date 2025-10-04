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
    val recognitionSensitivity: Float = 0.7f,    // 音声認識感度 (0.0-1.0)
    val speechRate: Float = 1.0f,                // 読み上げ速度 (0.5-2.0)
    val speechPitch: Float = 1.0f,               // 読み上げピッチ (0.5-2.0)
    val language: String = "ja-JP",              // 言語設定
    val isVoiceFeedbackEnabled: Boolean = true,  // 音声フィードバック有効
    val timeoutDuration: Long = 5000L,           // 音声認識タイムアウト (ms)
    val practiceMode: Boolean = false,           // 練習モード
    val emergencyModeEnabled: Boolean = true     // 緊急モード対応
)

/**
 * EntityからDomainモデルへの変換
 */
fun VoiceSettingEntity.toDomain(): VoiceSetting {
    return VoiceSetting(
        id = id,
        recognitionSensitivity = recognitionSensitivity,
        speechRate = speechRate,
        speechPitch = speechPitch,
        language = language,
        isVoiceFeedbackEnabled = isVoiceFeedbackEnabled,
        timeoutDuration = timeoutDuration,
        practiceMode = practiceMode,
        emergencyModeEnabled = emergencyModeEnabled
    )
}

/**
 * DomainモデルからEntityへの変換
 */
fun VoiceSetting.toEntity(): VoiceSettingEntity {
    return VoiceSettingEntity(
        id = id,
        recognitionSensitivity = recognitionSensitivity,
        speechRate = speechRate,
        speechPitch = speechPitch,
        language = language,
        isVoiceFeedbackEnabled = isVoiceFeedbackEnabled,
        timeoutDuration = timeoutDuration,
        practiceMode = practiceMode,
        emergencyModeEnabled = emergencyModeEnabled
    )
}
