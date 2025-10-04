package com.akira.carrycheck.data.repository

import com.akira.carrycheck.data.dao.VoiceSettingDao
import com.akira.carrycheck.data.entity.VoiceSettingEntity
import com.akira.carrycheck.data.model.VoiceSetting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 音声設定のリポジトリ
 * データモデルとエンティティの変換、ビジネスロジックを担当
 */
class VoiceSettingRepository(
    private val voiceSettingDao: VoiceSettingDao
) {

    /**
     * 全ての音声設定を取得
     */
    fun getAllSettings(): Flow<List<VoiceSetting>> {
        return voiceSettingDao.getAllSettings().map { entities ->
            entities.map { it.toModel() }
        }
    }

    /**
     * ユーザーIDで音声設定を取得
     */
    fun getSettingByUserId(userId: String): Flow<VoiceSetting?> {
        return voiceSettingDao.getSettingByUserId(userId).map { entity ->
            entity?.toModel()
        }
    }

    /**
     * ユーザーIDで音声設定を取得（suspend版）
     */
    suspend fun getSettingByUserIdSuspend(userId: String): VoiceSetting? {
        return voiceSettingDao.getSettingByUserIdSuspend(userId)?.toModel()
    }

    /**
     * IDで音声設定を取得
     */
    suspend fun getSettingById(id: Long): VoiceSetting? {
        return voiceSettingDao.getSettingById(id)?.toModel()
    }

    /**
     * 音声設定を挿入
     */
    suspend fun insertSetting(setting: VoiceSetting): Long {
        return voiceSettingDao.insertSetting(setting.toEntity())
    }

    /**
     * 複数の音声設定を挿入
     */
    suspend fun insertSettings(settings: List<VoiceSetting>): List<Long> {
        return voiceSettingDao.insertSettings(settings.map { it.toEntity() })
    }

    /**
     * 音声設定を更新
     */
    suspend fun updateSetting(setting: VoiceSetting) {
        voiceSettingDao.updateSetting(setting.toEntity())
    }

    /**
     * 音声設定を削除
     */
    suspend fun deleteSetting(setting: VoiceSetting) {
        voiceSettingDao.deleteSetting(setting.toEntity())
    }

    /**
     * IDで音声設定を削除
     */
    suspend fun deleteSettingById(id: Long) {
        voiceSettingDao.deleteSettingById(id)
    }

    /**
     * ユーザーIDで音声設定を削除
     */
    suspend fun deleteSettingByUserId(userId: String) {
        voiceSettingDao.deleteSettingByUserId(userId)
    }

    /**
     * 全ての音声設定を削除
     */
    suspend fun deleteAllSettings() {
        voiceSettingDao.deleteAllSettings()
    }
}

/**
 * VoiceSettingEntityからVoiceSettingへの変換
 */
private fun VoiceSettingEntity.toModel(): VoiceSetting {
    return VoiceSetting(
        id = this.id,
        userId = this.userId,
        isVoiceRecognitionEnabled = this.isVoiceRecognitionEnabled,
        voiceRecognitionLanguage = this.voiceRecognitionLanguage,
        maxRecognitionAttempts = this.maxRecognitionAttempts,
        isTtsEnabled = this.isTtsEnabled,
        ttsLanguage = this.ttsLanguage,
        ttsSpeed = this.ttsSpeed,
        ttsPitch = this.ttsPitch,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * VoiceSettingからVoiceSettingEntityへの変換
 */
private fun VoiceSetting.toEntity(): VoiceSettingEntity {
    return VoiceSettingEntity(
        id = this.id,
        userId = this.userId,
        isVoiceRecognitionEnabled = this.isVoiceRecognitionEnabled,
        voiceRecognitionLanguage = this.voiceRecognitionLanguage,
        maxRecognitionAttempts = this.maxRecognitionAttempts,
        isTtsEnabled = this.isTtsEnabled,
        ttsLanguage = this.ttsLanguage,
        ttsSpeed = this.ttsSpeed,
        ttsPitch = this.ttsPitch,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
