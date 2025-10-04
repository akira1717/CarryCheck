package com.akira.carrycheck.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * 音声設定のDAO
 * v3.0拡張: 高度音声設定管理
 */
@Dao
interface VoiceSettingDao {

    /**
     * 現在の音声設定取得（最新のもの）
     */
    @Query("SELECT * FROM voice_settings ORDER BY id DESC LIMIT 1")
    fun getCurrentVoiceSetting(): Flow<VoiceSettingEntity?>

    /**
     * IDで音声設定取得
     */
    @Query("SELECT * FROM voice_settings WHERE id = :id")
    suspend fun getVoiceSettingById(id: Long): VoiceSettingEntity?

    /**
     * 全音声設定履歴取得
     */
    @Query("SELECT * FROM voice_settings ORDER BY id DESC")
    fun getAllVoiceSettings(): Flow<List<VoiceSettingEntity>>

    /**
     * 言語設定で検索
     */
    @Query("SELECT * FROM voice_settings WHERE language = :language ORDER BY id DESC LIMIT 1")
    suspend fun getVoiceSettingByLanguage(language: String): VoiceSettingEntity?

    /**
     * 練習モード有効設定取得
     */
    @Query("SELECT * FROM voice_settings WHERE practiceMode = 1 ORDER BY id DESC LIMIT 1")
    suspend fun getPracticeModeSettings(): VoiceSettingEntity?

    /**
     * 緊急モード対応設定取得
     */
    @Query("SELECT * FROM voice_settings WHERE emergencyModeEnabled = 1 ORDER BY id DESC LIMIT 1")
    suspend fun getEmergencyModeSettings(): VoiceSettingEntity?

    /**
     * 音声設定挿入（新しい設定を保存）
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoiceSetting(setting: VoiceSettingEntity): Long

    /**
     * 音声設定更新
     */
    @Update
    suspend fun updateVoiceSetting(setting: VoiceSettingEntity)

    /**
     * 音声設定削除
     */
    @Delete
    suspend fun deleteVoiceSetting(setting: VoiceSettingEntity)

    /**
     * IDで音声設定削除
     */
    @Query("DELETE FROM voice_settings WHERE id = :id")
    suspend fun deleteVoiceSettingById(id: Long)

    /**
     * 古い音声設定削除（最新5件以外）
     */
    @Query("""
        DELETE FROM voice_settings 
        WHERE id NOT IN (
            SELECT id FROM voice_settings 
            ORDER BY id DESC LIMIT 5
        )
    """)
    suspend fun deleteOldVoiceSettings()

    /**
     * 全音声設定削除
     */
    @Query("DELETE FROM voice_settings")
    suspend fun deleteAllVoiceSettings()

    /**
     * デフォルト設定に戻す
     */
    @Query("DELETE FROM voice_settings")
    suspend fun resetToDefault()

    /**
     * 音声認識感度更新
     */
    @Query("UPDATE voice_settings SET recognitionSensitivity = :sensitivity WHERE id = :id")
    suspend fun updateRecognitionSensitivity(id: Long, sensitivity: Float)

    /**
     * 読み上げ速度更新
     */
    @Query("UPDATE voice_settings SET speechRate = :rate WHERE id = :id")
    suspend fun updateSpeechRate(id: Long, rate: Float)

    /**
     * 読み上げピッチ更新
     */
    @Query("UPDATE voice_settings SET speechPitch = :pitch WHERE id = :id")
    suspend fun updateSpeechPitch(id: Long, pitch: Float)

    /**
     * 言語設定更新
     */
    @Query("UPDATE voice_settings SET language = :language WHERE id = :id")
    suspend fun updateLanguage(id: Long, language: String)

    /**
     * 音声フィードバック有効/無効切り替え
     */
    @Query("UPDATE voice_settings SET isVoiceFeedbackEnabled = :enabled WHERE id = :id")
    suspend fun updateVoiceFeedbackEnabled(id: Long, enabled: Boolean)

    /**
     * 練習モード有効/無効切り替え
     */
    @Query("UPDATE voice_settings SET practiceMode = :enabled WHERE id = :id")
    suspend fun updatePracticeMode(id: Long, enabled: Boolean)

    /**
     * 緊急モード対応有効/無効切り替え
     */
    @Query("UPDATE voice_settings SET emergencyModeEnabled = :enabled WHERE id = :id")
    suspend fun updateEmergencyModeEnabled(id: Long, enabled: Boolean)

    /**
     * 音声設定数取得
     */
    @Query("SELECT COUNT(*) FROM voice_settings")
    suspend fun getVoiceSettingCount(): Int
}
