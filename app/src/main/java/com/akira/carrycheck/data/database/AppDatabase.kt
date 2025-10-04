package com.akira.carrycheck.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.akira.carrycheck.data.dao.ChecklistItemDao
import com.akira.carrycheck.data.dao.VoiceSettingDao
import com.akira.carrycheck.data.entity.ChecklistItemEntity
import com.akira.carrycheck.data.entity.VoiceSettingEntity

/**
 * CarryCheckアプリのRoomデータベース
 * バージョン1: 初期実装
 * - checklist_items テーブル
 * - voice_settings テーブル
 */
@Database(
    entities = [
        ChecklistItemEntity::class,
        VoiceSettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * チェックリストアイテムDAO取得
     */
    abstract fun checklistItemDao(): ChecklistItemDao

    /**
     * 音声設定DAO取得
     */
    abstract fun voiceSettingDao(): VoiceSettingDao

    companion object {
        /**
         * データベース名
         */
        private const val DATABASE_NAME = "carrycheck_database"

        /**
         * シングルトンインスタンス
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * データベースインスタンス取得
         * シングルトンパターンでスレッドセーフに実装
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * テスト用：データベースインスタンス破棄
         */
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
