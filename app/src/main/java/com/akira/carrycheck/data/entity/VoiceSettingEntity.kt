package com.akira.carrycheck.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 音声設定のRoomエンティティ
 * データベーステーブル：voice_settings
 */
@Entity(tableName = "voice_settings")
data class VoiceSettingEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "user_id")
    val userId: String = "default_user",

    @ColumnInfo(name = "is_voice_recognition_enabled")
    val isVoiceRecognitionEnabled: Boolean = true,

    @ColumnInfo(name = "voice_recognition_language")
    val voiceRecognitionLanguage: String = "ja-JP",

    @ColumnInfo(name = "max_recognition_attempts")
    val maxRecognitionAttempts: Int = 3,

    @ColumnInfo(name = "is_tts_enabled")
    val isTtsEnabled: Boolean = true,

    @ColumnInfo(name = "tts_language")
    val ttsLanguage: String = "ja-JP",

    @ColumnInfo(name = "tts_speed")
    val ttsSpeed: Float = 1.0f,

    @ColumnInfo(name = "tts_pitch")
    val ttsPitch: Float = 1.0f,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
