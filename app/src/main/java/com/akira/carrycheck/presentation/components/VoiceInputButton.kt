package com.akira.carrycheck.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * CarryCheck v3.0 音声入力ボタン
 * 段階式音声入力システムの中核コンポーネント
 */
@Composable
fun VoiceInputButton(
    isListening: Boolean,
    onStartVoiceInput: () -> Unit,
    onStopVoiceInput: () -> Unit,
    modifier: Modifier = Modifier
) {
    // リスニング中のアニメーション
    val infiniteTransition = rememberInfiniteTransition(label = "voice_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_animation"
    )

    // ボタンの色アニメーション
    val buttonColor by animateColorAsState(
        targetValue = if (isListening) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(300),
        label = "color_animation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // メイン音声入力ボタン
        FloatingActionButton(
            onClick = {
                if (isListening) {
                    onStopVoiceInput()
                } else {
                    onStartVoiceInput()
                }
            },
            containerColor = buttonColor,
            modifier = Modifier
                .size(80.dp)
                .scale(if (isListening) scale else 1f),
            shape = CircleShape
        ) {
            Icon(
                imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                contentDescription = if (isListening) "音声入力停止" else "音声入力開始",
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ステータステキスト
        Text(
            text = if (isListening) "聞いています..." else "タップして話す",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isListening) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )

        // リスニング中のインジケーター
        if (isListening) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                repeat(3) { index ->
                    val animationDelay = index * 200
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = 600,
                                delayMillis = animationDelay
                            ),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "indicator_$index"
                    )

                    Card(
                        modifier = Modifier
                            .size(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = alpha)
                        )
                    ) {}
                }
            }
        }
    }
}

/**
 * 音声入力ボタンのプレビュー用コンポーネント
 */
@Composable
fun VoiceInputButtonPreview(
    isListening: Boolean = false
) {
    VoiceInputButton(
        isListening = isListening,
        onStartVoiceInput = { },
        onStopVoiceInput = { }
    )
}
