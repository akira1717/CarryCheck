package com.akira.carrycheck.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 音声読み上げ（TTS: Text-To-Speech）ユーティリティクラス
 * CarryCheckの音声読み上げ機能を提供
 */
class TextToSpeechUtil(private val context: Context) {

    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    /**
     * TTSの結果
     */
    data class SpeechResult(
        val isSuccess: Boolean = true,
        val errorMessage: String? = null
    )

    /**
     * TTSの設定
     */
    data class SpeechConfig(
        val language: String = Constants.DEFAULT_LANGUAGE,
        val speed: Float = 1.0f,
        val pitch: Float = 1.0f,
        val queueMode: Int = TextToSpeech.QUEUE_FLUSH
    )

    /**
     * TTSエンジンを初期化
     */
    suspend fun initialize(): SpeechResult = suspendCancellableCoroutine { continuation ->
        textToSpeech = TextToSpeech(context) { status ->
            when (status) {
                TextToSpeech.SUCCESS -> {
                    isInitialized = true
                    if (continuation.isActive) {
                        continuation.resume(SpeechResult(isSuccess = true))
                    }
                }
                else -> {
                    isInitialized = false
                    if (continuation.isActive) {
                        continuation.resumeWithException(
                            IllegalStateException("TTS初期化に失敗しました（ステータス: $status）")
                        )
                    }
                }
            }
        }

        // キャンセル時の処理
        continuation.invokeOnCancellation {
            if (!isInitialized) {
                textToSpeech?.shutdown()
                textToSpeech = null
            }
        }
    }

    /**
     * TTSが初期化済みかチェック
     */
    fun isInitialized(): Boolean = isInitialized

    /**
     * 利用可能な言語かチェック
     */
    fun isLanguageAvailable(language: String): Boolean {
        if (!isInitialized) return false

        val locale = parseLanguageString(language)
        return when (textToSpeech?.isLanguageAvailable(locale)) {
            TextToSpeech.LANG_AVAILABLE,
            TextToSpeech.LANG_COUNTRY_AVAILABLE,
            TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE -> true
            else -> false
        }
    }

    /**
     * テキストを音声で読み上げ（コルーチン対応）
     * @param text 読み上げるテキスト
     * @param config TTS設定
     * @return 読み上げ結果
     */
    suspend fun speak(
        text: String,
        config: SpeechConfig = SpeechConfig()
    ): SpeechResult = suspendCancellableCoroutine { continuation ->

        if (!isInitialized) {
            continuation.resumeWithException(
                IllegalStateException("TTSが初期化されていません")
            )
            return@suspendCancellableCoroutine
        }

        if (text.isEmpty()) {
            continuation.resume(SpeechResult(isSuccess = false, errorMessage = "読み上げテキストが空です"))
            return@suspendCancellableCoroutine
        }

        if (text.length > Constants.MAX_TTS_TEXT_LENGTH) {
            continuation.resume(
                SpeechResult(
                    isSuccess = false,
                    errorMessage = "テキストが長すぎます（最大${Constants.MAX_TTS_TEXT_LENGTH}文字）"
                )
            )
            return@suspendCancellableCoroutine
        }

        // TTS設定を適用
        setupTtsConfig(config)

        // 発話ID生成
        val utteranceId = "carrycheck_${System.currentTimeMillis()}"

        // 進行状況リスナー設定
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                // 読み上げ開始
            }

            override fun onDone(utteranceId: String?) {
                if (continuation.isActive) {
                    continuation.resume(SpeechResult(isSuccess = true))
                }
            }

            override fun onError(utteranceId: String?) {
                if (continuation.isActive) {
                    continuation.resume(
                        SpeechResult(
                            isSuccess = false,
                            errorMessage = "音声読み上げエラーが発生しました"
                        )
                    )
                }
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                if (continuation.isActive) {
                    continuation.resume(
                        SpeechResult(
                            isSuccess = false,
                            errorMessage = "音声読み上げエラー（コード: $errorCode）"
                        )
                    )
                }
            }
        })

        // キャンセル時の処理
        continuation.invokeOnCancellation {
            textToSpeech?.stop()
        }

        // 読み上げ実行
        val result = textToSpeech?.speak(text, config.queueMode, null, utteranceId)

        if (result != TextToSpeech.SUCCESS) {
            if (continuation.isActive) {
                continuation.resume(
                    SpeechResult(
                        isSuccess = false,
                        errorMessage = "音声読み上げの開始に失敗しました"
                    )
                )
            }
        }
    }

    /**
     * 現在の読み上げを停止
     */
    fun stop() {
        textToSpeech?.stop()
    }

    /**
     * TTSが読み上げ中かチェック
     */
    fun isSpeaking(): Boolean {
        return textToSpeech?.isSpeaking == true
    }

    /**
     * リソースを解放
     */
    fun shutdown() {
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
    }

    /**
     * TTS設定を適用
     */
    private fun setupTtsConfig(config: SpeechConfig) {
        textToSpeech?.let { tts ->
            // 言語設定
            val locale = parseLanguageString(config.language)
            tts.language = locale

            // 速度設定（範囲制限）
            val speed = config.speed.coerceIn(Constants.MIN_TTS_SPEED, Constants.MAX_TTS_SPEED)
            tts.setSpeechRate(speed)

            // ピッチ設定（範囲制限）
            val pitch = config.pitch.coerceIn(Constants.MIN_TTS_PITCH, Constants.MAX_TTS_PITCH)
            tts.setPitch(pitch)
        }
    }

    /**
     * 言語文字列をLocaleに変換
     */
    private fun parseLanguageString(language: String): Locale {
        return when (language) {
            Constants.DEFAULT_LANGUAGE -> Locale.JAPAN
            Constants.LANGUAGE_ENGLISH -> Locale.US
            else -> {
                val parts = language.split("-")
                when (parts.size) {
                    1 -> Locale(parts[0])
                    2 -> Locale(parts[0], parts[1])
                    else -> Locale.getDefault()
                }
            }
        }
    }
}
