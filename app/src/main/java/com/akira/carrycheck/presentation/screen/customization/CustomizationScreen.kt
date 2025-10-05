package com.akira.carrycheck.presentation.screen.customization

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.akira.carrycheck.data.model.Season

/**
 * カスタマイズ画面
 * v3.0: 背景・言語・文字サイズ・テーマ・アクセシビリティ設定
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizationScreen(
    onNavigateBack: () -> Unit,
    viewModel: CustomizationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("カスタマイズ") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("←")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 背景カスタマイズ
            item {
                BackgroundCustomizationSection(
                    selectedSeason = uiState.selectedSeason,
                    onSeasonChanged = viewModel::setSelectedSeason // updateSeason → setSelectedSeason
                )
            }

            // テーマカスタマイズ
            item {
                ThemeCustomizationSection(
                    isDarkMode = uiState.isDarkMode,
                    onThemeChanged = viewModel::setDarkMode // updateTheme → setDarkMode
                )
            }

            // 言語カスタマイズ
            item {
                LanguageCustomizationSection(
                    selectedLanguage = uiState.selectedLanguage,
                    onLanguageChanged = viewModel::setSelectedLanguage // updateLanguage → setSelectedLanguage
                )
            }

            // フォントサイズカスタマイズ
            item {
                FontSizeCustomizationSection(
                    fontSize = uiState.fontSize,
                    onFontSizeChanged = viewModel::setFontSize // updateFontSize → setFontSize
                )
            }

            // キャラクターカスタマイズ
            item {
                CharacterCustomizationSection(
                    showCharacter = uiState.showCharacter,
                    characterVariation = uiState.characterVariation,
                    onShowCharacterChanged = viewModel::setShowCharacter, // updateShowCharacter → setShowCharacter
                    onCharacterVariationChanged = viewModel::setCharacterVariation // updateCharacterVariation → setCharacterVariation
                )
            }

            // アクセシビリティ設定
            item {
                AccessibilityCustomizationSection(
                    highContrast = uiState.highContrast,
                    largeText = uiState.largeText,
                    voiceGuidance = uiState.voiceGuidance,
                    onHighContrastChanged = viewModel::setHighContrast, // updateHighContrast → setHighContrast
                    onLargeTextChanged = viewModel::setLargeText, // updateLargeText → setLargeText
                    onVoiceGuidanceChanged = viewModel::setVoiceGuidance // updateVoiceGuidance → setVoiceGuidance
                )
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
    onSeasonChanged: (Season) -> Unit
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
                text = "背景設定",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val seasons = Season.values()
            seasons.forEach { season ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedSeason == season,
                            onClick = { onSeasonChanged(season) }
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedSeason == season,
                        onClick = { onSeasonChanged(season) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (season) {
                            Season.SPRING -> "春（桜・富士山）"
                            Season.SUMMER -> "夏（青空・雲）"
                            Season.AUTUMN -> "秋（紅葉）"
                            Season.WINTER -> "冬（雪景色）"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
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
    onThemeChanged: (Boolean) -> Unit
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
                text = "テーマ設定",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ダークモード",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = onThemeChanged
                )
            }
        }
    }
}

/**
 * 言語カスタマイズセクション
 */
@Composable
private fun LanguageCustomizationSection(
    selectedLanguage: String, // LanguageOption → String に変更
    onLanguageChanged: (String) -> Unit // LanguageOption → String に変更
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

            languages.forEach { (code, displayName) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedLanguage == code,
                            onClick = { onLanguageChanged(code) }
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedLanguage == code,
                        onClick = { onLanguageChanged(code) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/**
 * フォントサイズカスタマイズセクション
 */
@Composable
private fun FontSizeCustomizationSection(
    fontSize: Float,
    onFontSizeChanged: (Float) -> Unit
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
                text = "フォントサイズ",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "サンプルテキスト",
                fontSize = androidx.compose.ui.unit.sp * fontSize,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Slider(
                value = fontSize,
                onValueChange = onFontSizeChanged,
                valueRange = 0.8f..1.5f,
                steps = 6
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "小",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "大",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * キャラクターカスタマイズセクション
 */
@Composable
private fun CharacterCustomizationSection(
    showCharacter: Boolean,
    characterVariation: String,
    onShowCharacterChanged: (Boolean) -> Unit,
    onCharacterVariationChanged: (String) -> Unit
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
                text = "キャリーちゃん設定",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "キャリーちゃんを表示",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = showCharacter,
                    onCheckedChange = onShowCharacterChanged
                )
            }

            if (showCharacter) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "バリエーション",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val variations = listOf("標準", "季節限定", "お出かけ", "おうち")
                variations.forEach { variation ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = characterVariation == variation,
                                onClick = { onCharacterVariationChanged(variation) }
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = characterVariation == variation,
                            onClick = { onCharacterVariationChanged(variation) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = variation,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

/**
 * アクセシビリティカスタマイズセクション
 */
@Composable
private fun AccessibilityCustomizationSection(
    highContrast: Boolean,
    largeText: Boolean,
    voiceGuidance: Boolean,
    onHighContrastChanged: (Boolean) -> Unit,
    onLargeTextChanged: (Boolean) -> Unit,
    onVoiceGuidanceChanged: (Boolean) -> Unit
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
                text = "アクセシビリティ",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // ハイコントラスト
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ハイコントラスト",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = highContrast,
                    onCheckedChange = onHighContrastChanged
                )
            }

            // 大きな文字
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "大きな文字",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = largeText,
                    onCheckedChange = onLargeTextChanged
                )
            }

            // 音声ガイダンス
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "音声ガイダンス",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = voiceGuidance,
                    onCheckedChange = onVoiceGuidanceChanged
                )
            }
        }
    }
}
