package com.akira.carrycheck.presentation.screen.customization

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.akira.carrycheck.data.model.Season

/**
 * 言語オプション列挙型（ViewModelに合わせて定義）
 */
enum class LanguageOption {
    JAPANESE,
    ENGLISH_US,
    ENGLISH_UK,
    KOREAN,
    CHINESE_SIMPLIFIED,
    CHINESE_TRADITIONAL;

    companion object {
        fun getAllOptions(): Array<LanguageOption> = entries.toTypedArray()
    }
}

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
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "戻る"
                        )
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
                    selectedSeason = uiState.selectedSeason
                )
            }

            // テーマカスタマイズ
            item {
                ThemeCustomizationSection(
                    isDarkMode = uiState.isDarkMode
                )
            }

            // 言語カスタマイズ
            item {
                LanguageCustomizationSection(
                    selectedLanguage = uiState.selectedLanguage
                )
            }

            // フォントサイズカスタマイズ
            item {
                FontSizeCustomizationSection(
                    fontSize = uiState.fontSize,
                    onFontSizeChanged = viewModel::updateFontSize
                )
            }

            // キャラクターカスタマイズ
            item {
                CharacterCustomizationSection(
                    showCharacter = uiState.showCharacter
                )
            }

            // アクセシビリティ設定
            item {
                AccessibilityCustomizationSection()
            }
        }
    }
}

/**
 * 背景カスタマイズセクション
 */
@Composable
private fun BackgroundCustomizationSection(
    selectedSeason: Season
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

            Season.entries.forEach { season ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedSeason == season,
                            onClick = { /* TODO: 実装予定 */ }
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedSeason == season,
                        onClick = { /* TODO: 実装予定 */ }
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
    isDarkMode: Boolean
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
                    onCheckedChange = { /* TODO: 実装予定 */ }
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
    selectedLanguage: LanguageOption
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

            LanguageOption.entries.forEach { languageOption ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedLanguage == languageOption,
                            onClick = { /* TODO: 実装予定 */ }
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedLanguage == languageOption,
                        onClick = { /* TODO: 実装予定 */ }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (languageOption) {
                            LanguageOption.JAPANESE -> "日本語"
                            LanguageOption.ENGLISH_US -> "English (US)"
                            LanguageOption.ENGLISH_UK -> "English (UK)"
                            LanguageOption.KOREAN -> "한국어"
                            LanguageOption.CHINESE_SIMPLIFIED -> "中文 (简体)"
                            LanguageOption.CHINESE_TRADITIONAL -> "中文 (繁體)"
                        },
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
                fontSize = (14 * fontSize).sp,
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
    showCharacter: Boolean
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
                    onCheckedChange = { /* TODO: 実装予定 */ }
                )
            }
        }
    }
}

/**
 * アクセシビリティカスタマイズセクション
 */
@Composable
private fun AccessibilityCustomizationSection() {
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

            Text(
                text = "※ アクセシビリティ機能は今後のアップデートで実装予定です",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
