package com.akira.carrycheck.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.akira.carrycheck.data.entity.ChecklistItemEntity
import com.akira.carrycheck.data.entity.VoiceSettingEntity
import com.akira.carrycheck.data.dao.ChecklistItemDao
import com.akira.carrycheck.data.dao.VoiceSettingDao

/**
 * CarryCheck v3.0 メインデータベース
 * Phase2拡張: isImportantカラム追加、音声設定テーブル追加
 */
@Database(
    entities = [
        ChecklistItemEntity::class,
        VoiceSettingEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun checklistItemDao(): ChecklistItemDao
    abstract fun voiceSettingDao(): VoiceSettingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DATABASE_NAME = "carry_check_database"

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * データベースマイグレーション v1 -> v2
         * - checklist_itemsテーブルにisImportantカラム追加
         * - voice_settingsテーブル新規作成
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. checklist_itemsテーブルにisImportantカラム追加
                database.execSQL(
                    "ALTER TABLE checklist_items ADD COLUMN is_important INTEGER NOT NULL DEFAULT 0"
                )

                // 2. voice_settingsテーブル作成
                database.execSQL("""
                    CREATE TABLE voice_settings (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        user_id TEXT NOT NULL DEFAULT 'default_user',
                        is_voice_recognition_enabled INTEGER NOT NULL DEFAULT 1,
                        voice_recognition_language TEXT NOT NULL DEFAULT 'ja-JP',
                        max_recognition_attempts INTEGER NOT NULL DEFAULT 3,
                        is_tts_enabled INTEGER NOT NULL DEFAULT 1,
                        tts_language TEXT NOT NULL DEFAULT 'ja-JP',
                        tts_speed REAL NOT NULL DEFAULT 1.0,
                        tts_pitch REAL NOT NULL DEFAULT 1.0,
                        created_at INTEGER NOT NULL DEFAULT 0,
                        updated_at INTEGER NOT NULL DEFAULT 0
                    )
                """)

                // 3. デフォルト音声設定挿入
                database.execSQL("""
                    INSERT INTO voice_settings (
                        user_id, is_voice_recognition_enabled, voice_recognition_language,
                        max_recognition_attempts, is_tts_enabled, tts_language,
                        tts_speed, tts_pitch, created_at, updated_at
                    ) VALUES (
                        'default_user', 1, 'ja-JP', 3, 1, 'ja-JP', 1.0, 1.0, 
                        ${System.currentTimeMillis()}, ${System.currentTimeMillis()}
                    )
                """)
            }
        }
    }
}
