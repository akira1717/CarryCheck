package com.akira.carrycheck.data.dao

import androidx.room.*
import com.akira.carrycheck.data.entity.VoiceSettingEntity
import kotlinx.coroutines.flow.Flow

/**
 * 音声設定のDAO（Data Access Object）
 * VoiceSettingEntityに対するCRUD操作を定義
 */
@Dao
interface VoiceSettingDao {

    /**
     * 全ての音声設定を取得
     */
    @Query("SELECT * FROM voice_settings ORDER BY created_at DESC")
    fun getAllSettings(): Flow<List<VoiceSettingEntity>>

    /**
     * ユーザーIDで音声設定を取得
     */
    @Query("SELECT * FROM voice_settings WHERE user_id = :userId LIMIT 1")
    fun getSettingByUserId(userId: String): Flow<VoiceSettingEntity?>

    /**
     * ユーザーIDで音声設定を取得（suspend版）
     */
    @Query("SELECT * FROM voice_settings WHERE user_id = :userId LIMIT 1")
    suspend fun getSettingByUserIdSuspend(userId: String): VoiceSettingEntity?

    /**
     * IDで音声設定を取得
     */
    @Query("SELECT * FROM voice_settings WHERE id = :id")
    suspend fun getSettingById(id: Long): VoiceSettingEntity?

    /**
     * 音声設定を挿入
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: VoiceSettingEntity): Long

    /**
     * 複数の音声設定を挿入
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: List<VoiceSettingEntity>): List<Long>

    /**
     * 音声設定を更新
     */
    @Update
    suspend fun updateSetting(setting: VoiceSettingEntity)

    /**
     * 音声設定を削除
     */
    @Delete
    suspend fun deleteSetting(setting: VoiceSettingEntity)

    /**
     * IDで音声設定を削除
     */
    @Query("DELETE FROM voice_settings WHERE id = :id")
    suspend fun deleteSettingById(id: Long)

    /**
     * ユーザーIDで音声設定を削除
     */
    @Query("DELETE FROM voice_settings WHERE user_id = :userId")
    suspend fun deleteSettingByUserId(userId: String)

    /**
     * 全ての音声設定を削除
     */
    @Query("DELETE FROM voice_settings")
    suspend fun deleteAllSettings()
}
