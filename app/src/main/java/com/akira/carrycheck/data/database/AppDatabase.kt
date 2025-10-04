package com.akira.carrycheck.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context

/**
 * CarryCheck v3.0 メインデータベース
 * Phase2拡張: isImportantカラム追加、音声設定テーブル追加
 */
@Database(
    entities = [
        ChecklistItemEntity::class,
        VoiceSettingEntity::class
    ],
    version = 2, // Phase2でバージョンアップ
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
                    .addMigrations(MIGRATION_1_2) // マイグレーション追加
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
                    "ALTER TABLE checklist_items ADD COLUMN isImportant INTEGER NOT NULL DEFAULT 0"
                )

                // 2. voice_settingsテーブル作成
                database.execSQL("""
                    CREATE TABLE voice_settings (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        recognitionSensitivity REAL NOT NULL DEFAULT 0.7,
                        speechRate REAL NOT NULL DEFAULT 1.0,
                        speechPitch REAL NOT NULL DEFAULT 1.0,
                        language TEXT NOT NULL DEFAULT 'ja-JP',
                        isVoiceFeedbackEnabled INTEGER NOT NULL DEFAULT 1,
                        timeoutDuration INTEGER NOT NULL DEFAULT 5000,
                        practiceMode INTEGER NOT NULL DEFAULT 0,
                        emergencyModeEnabled INTEGER NOT NULL DEFAULT 1
                    )
                """)

                // 3. デフォルト音声設定挿入
                database.execSQL("""
                    INSERT INTO voice_settings (
                        recognitionSensitivity, speechRate, speechPitch, language,
                        isVoiceFeedbackEnabled, timeoutDuration, practiceMode, emergencyModeEnabled
                    ) VALUES (0.7, 1.0, 1.0, 'ja-JP', 1, 5000, 0, 1)
                """)
            }
        }

        /**
         * データベース初期化（開発・テスト用）
         */
        fun recreateDatabase(context: Context): AppDatabase {
            INSTANCE?.close()
            context.deleteDatabase(DATABASE_NAME)
            INSTANCE = null
            return getDatabase(context)
        }

        /**
         * データベースクローズ
         */
        fun closeDatabase() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}
