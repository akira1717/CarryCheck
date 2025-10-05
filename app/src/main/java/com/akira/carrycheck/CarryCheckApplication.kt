package com.akira.carrycheck

import android.app.Application
import androidx.room.Room
import com.akira.carrycheck.data.database.AppDatabase

class CarryCheckApplication : Application() {

    // データベースインスタンス（遅延初期化）
    val database by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "carrycheck_database"
        )
            .fallbackToDestructiveMigration() // 開発段階では破壊的マイグレーション許可
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        // アプリケーション全体の初期化処理
        initializeApp()
    }

    /**
     * アプリケーション初期化処理
     */
    private fun initializeApp() {
        // 音声認識エンジンの初期化準備
        // TextToSpeechエンジンの初期化準備
        // その他のグローバル設定
    }
}
