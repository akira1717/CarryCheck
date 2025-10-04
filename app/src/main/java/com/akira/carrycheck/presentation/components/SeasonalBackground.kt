package com.akira.carrycheck.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akira.carrycheck.data.model.Season
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * CarryCheck v3.0 季節背景コンポーネント
 * 日本の四季 + キャリーちゃん（白×グレーの猫）表示
 */
@Composable
fun SeasonalBackground(
    season: Season,
    modifier: Modifier = Modifier
) {
    // アニメーション設定
    val infiniteTransition = rememberInfiniteTransition(label = "seasonal_animation")
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "background_animation"
    )

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // 季節背景
        SeasonalBackgroundCanvas(
            season = season,
            animationProgress = animationProgress,
            modifier = Modifier.fillMaxSize()
        )

        // キャリーちゃん（右下角）
        CarryCharacter(
            season = season,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

/**
 * 季節背景Canvas
 */
@Composable
private fun SeasonalBackgroundCanvas(
    season: Season,
    animationProgress: Float,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    Canvas(modifier = modifier) {
        when (season) {
            Season.SPRING -> drawSpringBackground(animationProgress)
            Season.SUMMER -> drawSummerBackground(animationProgress)
            Season.AUTUMN -> drawAutumnBackground(animationProgress)
            Season.WINTER -> drawWinterBackground(animationProgress)
        }
    }
}

/**
 * 春背景（桜・富士山）
 */
private fun DrawScope.drawSpringBackground(progress: Float) {
    // グラデーション背景
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE3F2FD), // 薄い青
            Color(0xFFF8BBD9), // 薄いピンク
            Color(0xFFE8F5E8)  // 薄い緑
        )
    )
    drawRect(gradient)

    // 桜の花びら
    repeat(15) { i ->
        val x = (size.width * 0.1f) + (i * size.width * 0.06f) +
                (sin(progress * 2 * Math.PI + i) * 50).toFloat()
        val y = size.height * 0.3f + (cos(progress * 2 * Math.PI + i) * 100).toFloat()

        drawCircle(
            color = Color(0xFFFFB6C1), // 薄いピンク
            radius = 8f,
            center = Offset(x, y),
            alpha = 0.7f
        )
    }

    // 富士山シルエット
    drawTriangle(
        center = Offset(size.width * 0.8f, size.height * 0.7f),
        size = 150f,
        color = Color(0xFF9E9E9E).copy(alpha = 0.3f)
    )
}

/**
 * 夏背景（青空・雲）
 */
private fun DrawScope.drawSummerBackground(progress: Float) {
    // 青空グラデーション
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1976D2), // 濃い青
            Color(0xFF42A5F5), // 明るい青
            Color(0xFF81C784)  // 薄い緑
        )
    )
    drawRect(gradient)

    // 流れる雲
    repeat(5) { i ->
        val x = (size.width * progress + i * size.width * 0.3f) % (size.width + 100f) - 50f
        val y = size.height * 0.2f + i * 40f

        drawCircle(
            color = Color.White.copy(alpha = 0.8f),
            radius = 40f + i * 10f,
            center = Offset(x, y)
        )
    }

    // 太陽
    drawCircle(
        color = Color(0xFFFFD54F),
        radius = 50f,
        center = Offset(size.width * 0.8f, size.height * 0.15f),
        alpha = 0.9f
    )
}

/**
 * 秋背景（紅葉）
 */
private fun DrawScope.drawAutumnBackground(progress: Float) {
    // 秋空グラデーション
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFF8A65), // オレンジ
            Color(0xFFFFAB91), // 薄いオレンジ
            Color(0xFFFFF3E0)  // クリーム色
        )
    )
    drawRect(gradient)

    // 落ち葉
    repeat(20) { i ->
        val x = (size.width * 0.1f) + (i * size.width * 0.05f) +
                (sin(progress * 3 * Math.PI + i) * 80).toFloat()
        val y = (progress * size.height + i * 50f) % (size.height + 100f)

        val leafColor = when (i % 4) {
            0 -> Color(0xFFD84315) // 赤
            1 -> Color(0xFFFF8F00) // オレンジ
            2 -> Color(0xFFF57F17) // 黄色
            else -> Color(0xFF8D6E63) // 茶色
        }

        drawCircle(
            color = leafColor,
            radius = 6f + (i % 3) * 2f,
            center = Offset(x, y),
            alpha = 0.8f
        )
    }
}

/**
 * 冬背景（雪景色）
 */
private fun DrawScope.drawWinterBackground(progress: Float) {
    // 冬空グラデーション
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF607D8B), // グレー
            Color(0xFFB0BEC5), // 薄いグレー
            Color(0xFFECEFF1)  // ほぼ白
        )
    )
    drawRect(gradient)

    // 雪の結晶
    repeat(25) { i ->
        val x = (i * size.width * 0.04f + sin(progress * 2 * Math.PI + i) * 30).toFloat()
        val y = ((progress * size.height * 0.5f + i * 40f) % (size.height + 50f))

        drawCircle(
            color = Color.White,
            radius = 4f + (i % 3),
            center = Offset(x, y),
            alpha = 0.9f
        )
    }
}

/**
 * キャリーちゃん（白×グレーの猫）
 */
@Composable
private fun CarryCharacter(
    season: Season,
    modifier: Modifier = Modifier
) {
    // 季節に応じたキャリーちゃんのバリエーション
    val carryEmoji = when (season) {
        Season.SPRING -> "🐱🌸" // 桜と一緒
        Season.SUMMER -> "🐱☀️" // 太陽と一緒
        Season.AUTUMN -> "🐱🍂" // 落ち葉と一緒
        Season.WINTER -> "🐱❄️" // 雪と一緒
    }

    // ゆらゆらアニメーション
    val infiniteTransition = rememberInfiniteTransition(label = "carry_animation")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "carry_float"
    )

    Box(
        modifier = modifier
            .size(80.dp)
            .offset(y = offsetY.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = carryEmoji,
            fontSize = 40.sp,
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
    }
}

/**
 * 三角形描画ヘルパー
 */
private fun DrawScope.drawTriangle(
    center: Offset,
    size: Float,
    color: Color
) {
    val path = androidx.compose.ui.graphics.Path()
    path.moveTo(center.x, center.y - size / 2)
    path.lineTo(center.x - size / 2, center.y + size / 2)
    path.lineTo(center.x + size / 2, center.y + size / 2)
    path.close()

    drawPath(path, color)
}
