package com.akira.carrycheck.presentation.screen.customization

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akira.carrycheck.data.model.Season
import com.akira.carrycheck.presentation.components.SeasonalBackground

/**
 * CarryCheck v3.0 カスタマイズ画面
 * 背景・言語・文字サイズ・キャラクター設定
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizationScreen(
    onNavigateBack: () -> Unit,
    viewModel: CustomizationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // プレビュー背景
        SeasonalBackground(season = uiState.selectedSeason)

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // トップアプリバー
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("カスタマイズ", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                    }
                },
                actions = {
                    // リセットボタン
                    TextButton(onClick = { viewModel.resetToDefault() }) {
                        Text("リセット")
                    }
                }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 背景設定セクション
                item {
                    BackgroundCustomizationSection(
                        selectedSeason = uiState.selectedSeason,
                        onSeasonSelected = { viewModel.selectSeason(it) },
                        backgroundStyle = uiState.backgroundStyle,
                        onBackgroundStyleSelected = { viewModel.selectBackgroundStyle(it) }
                    )
                }

                // テーマ設定セクション
                item {
                    ThemeCustomizationSection(
                        isDarkMode = uiState.isDarkMode,
                        onDarkModeToggle = { viewModel.toggleDarkMode() },
                        isHighContrastMode = uiState.isHighContrastMode,
                        onHighContrastToggle = { viewModel.toggleHighContrast() },
                        accentColor = uiState.accentColor,
                        onAccentColorSelected = { viewModel.selectAccentColor(it) }
                    )
                }

                // 言語設定セクション
                item {
                    LanguageCustomizationSection(
                        selectedLanguage = uiState.selectedLanguage,
                        onLanguageSelected = { viewModel.selectLanguage(it) },
                        availableLanguages = uiState.availableLanguages
                    )
                }

                // フォント・文字サイズ設定セクション
                item {
                    FontCustomizationSection(
                        fontSize = uiState.fontSize,
                        onFontSizeChanged = { viewModel.updateFontSize(it) },
                        fontWeight = uiState.fontWeight,
                        onFontWeightSelected = { viewModel.selectFontWeight(it) }
                    )
                }

                // キャリーちゃん設定セクション
                item {
                    CharacterCustomizationSection(
                        characterStyle = uiState.characterStyle,
                        onCharacterStyleSelected = { viewModel.selectCharacterStyle(it) },
                        showCharacter = uiState.showCharacter,
                        onShowCharacterToggle = { viewModel.toggleShowCharacter() },
                        characterPosition = uiState.characterPosition,
                        onCharacterPositionSelected = { viewModel.selectCharacterPosition(it) }
                    )
                }

                // アクセシビリティ設定セクション
                item {
                    AccessibilityCustomizationSection(
                        reducedMotion = uiState.reducedMotion,
                        onReducedMotionToggle = { viewModel.toggleReducedMotion() },
                        screenReaderOptimized = uiState.screenReaderOptimized,
                        onScreenReaderOptimizedToggle = { viewModel.toggleScreenReaderOptimized() },
                        largeClickTargets = uiState.largeClickTargets,
                        onLargeClickTargetsToggle = { viewModel.toggleLargeClickTargets() }
                    )
                }

                // プレビューセクション
                item {
                    PreviewSection(
                        uiState = uiState,
                        onPreviewTest = { viewModel.testPreview() }
                    )
                }
            }
        }
    }
}

/**
 * 背景カスタマイズセクション
 */
@Composable
private fun BackgroundCustomizationSection(
    selectedSeason: Season,
    onSeasonSelected: (Season) -> Unit,
    backgroundStyle: BackgroundStyle,
    onBackgroundStyleSelected: (BackgroundStyle) -> Unit
) {
    CustomizationCard(
        title = "背景設定",
        icon = Icons.Default.Landscape
    ) {
        Column {
            Text(
                text = "季節テーマ",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Season.values().forEach { season ->
                    FilterChip(
                        selected = selectedSeason == season,
                        onClick = { onSeasonSelected(season) },
                        label = { Text(getSeasonDisplayName(season)) },
                        leadingIcon = {
                            Text(getSeasonEmoji(season))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "背景スタイル",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column {
                BackgroundStyle.values().forEach { style ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = backgroundStyle == style,
                                onClick = { onBackgroundStyleSelected(style) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = backgroundStyle == style,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = style.displayName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = style.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * テーマカスタマイズセクション
 */
@Composable
private fun ThemeCustomizationSection(
    isDarkMode: Boolean,
    onDarkModeToggle: () -> Unit,
    isHighContrastMode: Boolean,
    onHighContrastToggle: () -> Unit,
    accentColor: AccentColor,
    onAccentColorSelected: (AccentColor) -> Unit
) {
    CustomizationCard(
        title = "テーマ設定",
        icon = Icons.Default.DarkMode
    ) {
        Column {
            // ダークモード切り替え
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ダークモード",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "画面を暗くして目の負担を軽減",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { onDarkModeToggle() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 高コントラストモード切り替え
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "高コントラストモード",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "文字やボタンを見やすくします",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Switch(
                    checked = isHighContrastMode,
                    onCheckedChange = { onHighContrastToggle() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // アクセントカラー選択
            Text(
                text = "アクセントカラー",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AccentColor.values().forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(color.colorValue))
                            .selectable(
                                selected = accentColor == color,
                                onClick = { onAccentColorSelected(color) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (accentColor == color) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "選択中",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * カスタマイズカード共通コンポーネント
 */
@Composable
private fun CustomizationCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            content()
        }
    }
}

// ヘルパー関数とデータクラス
private fun getSeasonDisplayName(season: Season): String {
    return when (season) {
        Season.SPRING -> "春"
        Season.SUMMER -> "夏"
        Season.AUTUMN -> "秋"
        Season.WINTER -> "冬"
    }
}

private fun getSeasonEmoji(season: Season): String {
    return when (season) {
        Season.SPRING -> "🌸"
        Season.SUMMER -> "☀️"
        Season.AUTUMN -> "🍂"
        Season.WINTER -> "❄️"
    }
}
/**
 * 言語カスタマイズセクション
 */
@Composable
private fun LanguageCustomizationSection(
    selectedLanguage: String,
    onLanguageChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "言語設定",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val languages = listOf(
                "ja-JP" to "日本語",
                "en-US" to "English (US)",
                "en-GB" to "English (UK)",
                "ko-KR" to "한국어",
                "zh-CN" to "中文 (简体)",
                "zh-TW" to "中文 (繁體)"
            )

            languages.forEach { (code, name) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedLanguage == code,
                        onClick = { onLanguageChanged(code) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// 残りのセクション（Language, Font, Character, Accessibility, Preview）とenumは次のファイルで定義予定
