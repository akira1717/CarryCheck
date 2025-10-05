package com.akira.carrycheck.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.akira.carrycheck.data.model.ChecklistItem
import com.akira.carrycheck.data.model.Season


/**
 * CarryCheck v3.0 チェックリストアイテムカード
 * アクセシビリティ対応とアニメーション強化
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistItemCard(
    item: ChecklistItem,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    // チェック状態に応じたアニメーション
    val alpha by animateFloatAsState(
        targetValue = if (item.isChecked) 0.6f else 1f,
        animationSpec = tween(300),
        label = "alpha_animation"
    )

    // 季節に応じたカードの色
    val cardColors = getSeasonalCardColors(item.season)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (item.isChecked) 2.dp else 4.dp
        ),
        colors = cardColors,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // チェックボックス
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            // アイテム名
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (item.isChecked) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    },
                    color = if (item.isChecked) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                // 作成日時（小さく表示）
                Text(
                    text = formatCreatedTime(item.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 季節アイコン
            SeasonIcon(
                season = item.season,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 削除ボタン
            IconButton(
                onClick = onDeleteItem,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "削除",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * 季節に応じたカードの色を取得
 */
@Composable
private fun getSeasonalCardColors(season: Season): CardColors {
    return when (season) {
        Season.SPRING -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
        Season.SUMMER -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Season.AUTUMN -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
        Season.WINTER -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

/**
 * 季節アイコン
 */
@Composable
private fun SeasonIcon(
    season: Season,
    modifier: Modifier = Modifier
) {
    val emoji = when (season) {
        Season.SPRING -> "🌸"
        Season.SUMMER -> "☀️"
        Season.AUTUMN -> "🍂"
        Season.WINTER -> "❄️"
    }

    Text(
        text = emoji,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge
    )
}

/**
 * 作成時間のフォーマット
 */
private fun formatCreatedTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "今"
        diff < 3600_000 -> "${diff / 60_000}分前"
        diff < 86400_000 -> "${diff / 3600_000}時間前"
        else -> "${diff / 86400_000}日前"
    }
}

/**
 * プレビュー用コンポーネント
 */
@Composable
fun ChecklistItemCardPreview() {
    val sampleItem = ChecklistItem(
        id = 1,
        name = "財布",
        isChecked = false,
        season = Season.SPRING,
        createdAt = System.currentTimeMillis()
    )

    ChecklistItemCard(
        item = sampleItem,
        onCheckedChange = { },
        onDeleteItem = { }
    )
}
