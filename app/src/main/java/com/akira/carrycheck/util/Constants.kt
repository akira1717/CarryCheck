package com.akira.carrycheck.util

/**
 * CarryCheckアプリ全体で使用する定数定義
 */
object Constants {

    // ===== データベース関連定数 =====

    /**
     * データベース名
     */
    const val DATABASE_NAME = "carrycheck_database"

    /**
     * データベースバージョン
     */
    const val DATABASE_VERSION = 1

    // ===== 音声認識関連定数 =====

    /**
     * 音声認識の最大試行回数（デフォルト）
     */
    const val DEFAULT_MAX_RECOGNITION_ATTEMPTS = 3

    /**
     * 音声認識のタイムアウト（ミリ秒）
     */
    const val VOICE_RECOGNITION_TIMEOUT_MS = 5000L

    /**
     * 音声認識の最小音声長（ミリ秒）
     */
    const val MIN_VOICE_LENGTH_MS = 1000L

    // ===== TTS（音声読み上げ）関連定数 =====

    /**
     * TTSの最大文字数
     */
    const val MAX_TTS_TEXT_LENGTH = 4000

    /**
     * TTS速度の最小値
     */
    const val MIN_TTS_SPEED = 0.5f

    /**
     * TTS速度の最大値
     */
    const val MAX_TTS_SPEED = 2.0f

    /**
     * TTSピッチの最小値
     */
    const val MIN_TTS_PITCH = 0.5f

    /**
     * TTSピッチの最大値
     */
    const val MAX_TTS_PITCH = 2.0f

    // ===== チェックリスト関連定数 =====

    /**
     * アイテム優先度：高
     */
    const val PRIORITY_HIGH = 1

    /**
     * アイテム優先度：中（デフォルト）
     */
    const val PRIORITY_MEDIUM = 2

    /**
     * アイテム優先度：低
     */
    const val PRIORITY_LOW = 3

    /**
     * アイテム名の最大文字数
     */
    const val MAX_ITEM_NAME_LENGTH = 100

    /**
     * カテゴリ名の最大文字数
     */
    const val MAX_CATEGORY_NAME_LENGTH = 50

    // ===== 言語・地域関連定数 =====

    /**
     * デフォルト言語（日本語）
     */
    const val DEFAULT_LANGUAGE = "ja-JP"

    /**
     * 英語
     */
    const val LANGUAGE_ENGLISH = "en-US"

    // ===== SharedPreferences関連定数 =====

    /**
     * 設定保存用のPreferences名
     */
    const val PREFS_NAME = "carrycheck_preferences"

    /**
     * 初回起動フラグのキー
     */
    const val PREF_KEY_FIRST_LAUNCH = "first_launch"

    /**
     * 音声設定のユーザーIDキー
     */
    const val PREF_KEY_USER_ID = "user_id"

    // ===== 通知関連定数 =====

    /**
     * 通知チャンネルID
     */
    const val NOTIFICATION_CHANNEL_ID = "carrycheck_reminders"

    /**
     * 通知チャンネル名
     */
    const val NOTIFICATION_CHANNEL_NAME = "持ち物リマインダー"

    // ===== タイムアウト・遅延関連定数 =====

    /**
     * スプラッシュ画面表示時間（ミリ秒）
     */
    const val SPLASH_DELAY_MS = 2000L

    /**
     * ネットワークタイムアウト（ミリ秒）
     */
    const val NETWORK_TIMEOUT_MS = 10000L

    /**
     * アニメーション時間（ミリ秒）
     */
    const val ANIMATION_DURATION_MS = 300L
}
