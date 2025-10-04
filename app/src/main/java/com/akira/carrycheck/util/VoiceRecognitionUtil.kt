package com.akira.carrycheck.util

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 音声認識ユーティリティクラス
 * CarryCheckの段階式音声認識機能を提供
 */
class VoiceRecognitionUtil(private val context: Context) {

    /**
     * 音声認識の結果
     */
    data class RecognitionResult(
        val text: String,
        val confidence: Float = 0.0f,
        val isSuccess: Boolean = true,
        val errorMessage: String? = null
    )

    /**
     * 音声認識の設定
     */
    data class RecognitionConfig(
        val language: String = Constants.DEFAULT_LANGUAGE,
        val maxResults: Int = 1,
        val partialResults: Boolean = false,
        val timeoutMs: Long = Constants.VOICE_RECOGNITION_TIMEOUT_MS
    )

    /**
     * 音声認識が利用可能かチェック
     */
    fun isVoiceRecognitionAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    /**
     * 音声認識を実行（コルーチン対応）
     * @param config 音声認識の設定
     * @return 認識結果
     */
    suspend fun recognizeSpeech(
        config: RecognitionConfig = RecognitionConfig()
    ): RecognitionResult = suspendCancellableCoroutine { continuation ->

        if (!isVoiceRecognitionAvailable()) {
            continuation.resumeWithException(
                IllegalStateException("音声認識が利用できません")
            )
            return@suspendCancellableCoroutine
        }

        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val intent = createRecognitionIntent(config)

        speechRecognizer.setRecognitionListener(object : android.speech.RecognitionListener {
            override fun onReadyForSpeech(params: android.os.Bundle?) {
                // 音声入力準備完了
            }

            override fun onBeginningOfSpeech() {
                // 音声入力開始
            }

            override fun onRmsChanged(rmsdB: Float) {
                // 音声レベル変化（必要に応じて処理）
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // 音声バッファ受信（必要に応じて処理）
            }

            override fun onEndOfSpeech() {
                // 音声入力終了
            }

            override fun onError(error: Int) {
                val errorMessage = getErrorMessage(error)
                if (continuation.isActive) {
                    continuation.resume(
                        RecognitionResult(
                            text = "",
                            isSuccess = false,
                            errorMessage = errorMessage
                        )
                    )
                }
                speechRecognizer.destroy()
            }

            override fun onResults(results: android.os.Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val confidences = results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)

                if (!matches.isNullOrEmpty()) {
                    val text = matches[0]
                    val confidence = confidences?.getOrNull(0) ?: 0.0f

                    if (continuation.isActive) {
                        continuation.resume(
                            RecognitionResult(
                                text = text,
                                confidence = confidence,
                                isSuccess = true
                            )
                        )
                    }
                } else {
                    if (continuation.isActive) {
                        continuation.resume(
                            RecognitionResult(
                                text = "",
                                isSuccess = false,
                                errorMessage = "音声を認識できませんでした"
                            )
                        )
                    }
                }
                speechRecognizer.destroy()
            }

            override fun onPartialResults(partialResults: android.os.Bundle?) {
                // 部分結果（必要に応じて処理）
            }

            override fun onEvent(eventType: Int, params: android.os.Bundle?) {
                // イベント（必要に応じて処理）
            }
        })

        // キャンセル時の処理
        continuation.invokeOnCancellation {
            speechRecognizer.destroy()
        }

        // 音声認識開始
        speechRecognizer.startListening(intent)
    }

    /**
     * 段階式音声認識（指定回数まで試行）
     * @param maxAttempts 最大試行回数
     * @param config 音声認識の設定
     * @return 認識結果
     */
    suspend fun recognizeSpeechWithRetry(
        maxAttempts: Int = Constants.DEFAULT_MAX_RECOGNITION_ATTEMPTS,
        config: RecognitionConfig = RecognitionConfig()
    ): RecognitionResult {
        repeat(maxAttempts) { attempt ->
            try {
                val result = recognizeSpeech(config)
                if (result.isSuccess && result.text.isNotBlank()) {
                    return result
                }
            } catch (e: Exception) {
                if (attempt == maxAttempts - 1) {
                    return RecognitionResult(
                        text = "",
                        isSuccess = false,
                        errorMessage = "音声認識に失敗しました: ${e.message}"
                    )
                }
            }
        }

        return RecognitionResult(
            text = "",
            isSuccess = false,
            errorMessage = "音声認識が${maxAttempts}回とも失敗しました"
        )
    }

    /**
     * 音声認識用のIntentを作成
     */
    private fun createRecognitionIntent(config: RecognitionConfig): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, config.language)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, config.maxResults)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, config.partialResults)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, config.timeoutMs)
        }
    }

    /**
     * エラーコードをメッセージに変換
     */
    private fun getErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "オーディオエラーが発生しました"
            SpeechRecognizer.ERROR_CLIENT -> "クライアントエラーが発生しました"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "音声認識の権限がありません"
            SpeechRecognizer.ERROR_NETWORK -> "ネットワークエラーが発生しました"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "ネットワークタイムアウトしました"
            SpeechRecognizer.ERROR_NO_MATCH -> "音声を認識できませんでした"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "音声認識エンジンがビジーです"
            SpeechRecognizer.ERROR_SERVER -> "サーバーエラーが発生しました"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "音声入力がタイムアウトしました"
            else -> "不明なエラーが発生しました（コード: $errorCode）"
        }
    }
}
