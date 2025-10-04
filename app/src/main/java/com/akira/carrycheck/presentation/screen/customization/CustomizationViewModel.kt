package com.akira.carrycheck.presentation.screen.customization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.akira.carrycheck.data.model.Season
import com.akira.carrycheck.utils.TextToSpeechUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import java.util.*

/**
 * CarryCheck v3.0 カスタマイズViewModel
 * 背景・言語・文字サイズ・キャラクター・アクセシビリティ設定管理
 */
@HiltViewModel
class CustomizationViewModel @Inject constructor(
    private val textToSpeechUtil: TextToSpeechUtil
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomizationUiState())
    val uiState: StateFlow<CustomizationUiState> = _uiState.asStateFlow()

    init {
        loadCurrentSettings()
        initializeAvailableLanguages()
    }

    /**
     * 現在の設定読み込み
     */
    private fun loadCurrentSettings() {
        // TODO: SharedPreferencesまたはDataStoreから設定読み込み
        // 現在はデフォルト値を使用
        val currentSeason = getCurrentSeason()
        _uiState.value = _uiState.value.copy(
            selectedSeason = currentSeason
        )
    }

    /**
     * 利用可能言語初期化
     */
    private fun initializeAvailableLanguages() {
        val languages = listOf(
            LanguageOption("ja-JP", "日本語", "🇯🇵"),
            LanguageOption("en-US", "English (US)", "🇺🇸"),
            LanguageOption("en-GB", "English (UK)", "🇬🇧"),
            LanguageOption("ko-KR", "한국어", "🇰🇷"),
            LanguageOption("zh-CN", "中文 (简体)", "🇨🇳"),
            LanguageOption("zh-TW", "中文 (繁體)", "🇹🇼")
        )
        _uiState.value = _uiState.value.copy(availableLanguages = languages)
    }

    /**
     * 季節選択
     */
    fun selectSeason(season: Season) {
        _uiState.value = _uiState.value.copy(selectedSeason = season)
        saveSettings()
        textToSpeechUtil.speak("${getSeasonDisplayName(season)}テーマを選択しました")
    }

    /**
     * 背景スタイル選択
     */
    fun selectBackgroundStyle(style: BackgroundStyle) {
        _uiState.value = _uiState.value.copy(backgroundStyle = style)
        saveSettings()
        textToSpeechUtil.speak("${style.displayName}を選択しました")
    }

    /**
     * ダークモード切り替え
     */
    fun toggleDarkMode() {
        val newValue = !_uiState.value.isDarkMode
        _uiState.value = _uiState.value.copy(isDarkMode = newValue)
        saveSettings()
        textToSpeechUtil.speak(if (newValue) "ダークモードを有効にしました" else "ダークモードを無効にしました")
    }

    /**
     * 高コントラストモード切り替え
     */
    fun toggleHighContrast() {
        val newValue = !_uiState.value.isHighContrastMode
        _uiState.value = _uiState.value.copy(isHighContrastMode = newValue)
        saveSettings()
        textToSpeechUtil.speak(if (newValue) "高コントラストモードを有効にしました" else "高コントラストモードを無効にしました")
    }

    /**
     * アクセントカラー選択
     */
    fun selectAccentColor(color: AccentColor) {
        _uiState.value = _uiState.value.copy(accentColor = color)
        saveSettings()
        textToSpeechUtil.speak("${color.displayName}を選択しました")
    }

    /**
     * 言語選択
     */
    fun selectLanguage(language: LanguageOption) {
        _uiState.value = _uiState.value.copy(selectedLanguage = language)
        saveSettings()
        // 音声エンジンの言語も変更
        textToSpeechUtil.setLanguage(language.code)
        textToSpeechUtil.speak("言語を${language.displayName}に変更しました")
    }

    /**
     * フォントサイズ更新
     */
    fun updateFontSize(size: Float) {
        _uiState.value = _uiState.value.copy(fontSize = size)
        saveSettings()
    }

    /**
     * フォント太さ選択
     */
    fun selectFontWeight(weight: FontWeightOption) {
        _uiState.value = _uiState.value.copy(fontWeight = weight)
        saveSettings()
        textToSpeechUtil.speak("フォントの太さを${weight.displayName}に変更しました")
    }

    /**
     * キャラクタースタイル選択
     */
    fun selectCharacterStyle(style: CharacterStyle) {
        _uiState.value = _uiState.value.copy(characterStyle = style)
        saveSettings()
        textToSpeechUtil.speak("キャリーちゃんのスタイルを${style.displayName}に変更しました")
    }

    /**
     * キャラクター表示切り替え
     */
    fun toggleShowCharacter() {
        val newValue = !_uiState.value.showCharacter
        _uiState.value = _uiState.value.copy(showCharacter = newValue)
        saveSettings()
        textToSpeechUtil.speak(if (newValue) "キャリーちゃんを表示します" else "キャリーちゃんを非表示にします")
    }

    /**
     * キャラクターポジション選択
     */
    fun selectCharacterPosition(position: CharacterPosition) {
        _uiState.value = _uiState.value.copy(characterPosition = position)
        saveSettings()
        textToSpeechUtil.speak("キャリーちゃんの位置を${position.displayName}に変更しました")
    }

    /**
     * アニメーション軽減切り替え
     */
    fun toggleReducedMotion() {
        val newValue = !_uiState.value.reducedMotion
        _uiState.value = _uiState.value.copy(reducedMotion = newValue)
        saveSettings()
        textToSpeechUtil.speak(if (newValue) "アニメーションを軽減します" else "アニメーションを標準に戻します")
    }

    /**
     * スクリーンリーダー最適化切り替え
     */
    fun toggleScreenReaderOptimized() {
        val newValue = !_uiState.value.screenReaderOptimized
        _uiState.value = _uiState.value.copy(screenReaderOptimized = newValue)
        saveSettings()
        textToSpeechUtil.speak(if (newValue) "スクリーンリーダー最適化を有効にしました" else "スクリーンリーダー最適化を無効にしました")
    }

    /**
     * 大きなクリック領域切り替え
     */
    fun toggleLargeClickTargets() {
        val newValue = !_uiState.value.largeClickTargets
        _uiState.value = _uiState.value.copy(largeClickTargets = newValue)
        saveSettings()
        textToSpeechUtil.speak(if (newValue) "ボタンを大きくしました" else "ボタンを標準サイズに戻しました")
    }

    /**
     * デフォルト設定にリセット
     */
    fun resetToDefault() {
        _uiState.value = CustomizationUiState(
            selectedSeason = getCurrentSeason(),
            availableLanguages = _uiState.value.availableLanguages
        )
        saveSettings()
        textToSpeechUtil.speak("設定をデフォルトに戻しました")
    }

    /**
     * プレビューテスト
     */
    fun testPreview() {
        textToSpeechUtil.speak("これは設定のプレビューテストです。キャリーちゃんと一緒に持ち物をチェックしましょう！")
    }

    /**
     * 設定保存
     */
    private fun saveSettings() {
        viewModelScope.launch {
            // TODO: SharedPreferencesまたはDataStoreに設定保存
            // 現在は何もしない（メモリ内のみ）
        }
    }

    /**
     * 現在の季節取得
     */
    private fun getCurrentSeason(): Season {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        return when (currentMonth) {
            Calendar.MARCH, Calendar.APRIL, Calendar.MAY -> Season.SPRING
            Calendar.JUNE, Calendar.JULY, Calendar.AUGUST -> Season.SUMMER
            Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER -> Season.AUTUMN
            else -> Season.WINTER
        }
    }

    /**
     * 季節表示名取得
     */
    private fun getSeasonDisplayName(season: Season): String {
        return when (season) {
            Season.SPRING -> "春"
            Season.SUMMER -> "夏"
            Season.AUTUMN -> "秋"
            Season.WINTER -> "冬"
        }
    }
}

/**
 * カスタマイズ画面のUI状態
 */
data class CustomizationUiState(
    // 背景設定
    val selectedSeason: Season = Season.SPRING,
    val backgroundStyle: BackgroundStyle = BackgroundStyle.ANIMATED,

    // テーマ設定
    val isDarkMode: Boolean = false,
    val isHighContrastMode: Boolean = false,
    val accentColor: AccentColor = AccentColor.BLUE,

    // 言語設定
    val selectedLanguage: LanguageOption = LanguageOption("ja-JP", "日本語", "🇯🇵"),
    val availableLanguages: List<LanguageOption> = emptyList(),

    // フォント設定
    val fontSize: Float = 16f,
    val fontWeight: FontWeightOption = FontWeightOption.NORMAL,

    // キャラクター設定
    val characterStyle: CharacterStyle = CharacterStyle.CUTE,
    val showCharacter: Boolean = true,
    val characterPosition: CharacterPosition = CharacterPosition.BOTTOM_RIGHT,

    // アクセシビリティ設定
    val reducedMotion: Boolean = false,
    val screenReaderOptimized: Boolean = false,
    val largeClickTargets: Boolean = false
)

/**
 * 背景スタイル
 */
enum class BackgroundStyle(val displayName: String, val description: String) {
    ANIMATED("アニメーション", "動きのある背景"),
    STATIC("静止画", "シンプルな背景"),
    MINIMAL("ミニマル", "最小限の背景")
}

/**
 * アクセントカラー
 */
enum class AccentColor(val displayName: String, val colorValue: Long) {
    BLUE("ブルー", 0xFF2196F3),
    GREEN("グリーン", 0xFF4CAF50),
    ORANGE("オレンジ", 0xFFFF9800),
    PURPLE("パープル", 0xFF9C27B0),
    RED("レッド", 0xFFF44336),
    TEAL("ティール", 0xFF009688)
}

/**
 * 言語オプション
 */
data class LanguageOption(
    val code: String,
    val displayName: String,
    val flag: String
)

/**
 * フォント太さオプション
 */
enum class FontWeightOption(val displayName: String, val weight: androidx.compose.ui.text.font.FontWeight) {
    LIGHT("細い", androidx.compose.ui.text.font.FontWeight.Light),
    NORMAL("標準", androidx.compose.ui.text.font.FontWeight.Normal),
    MEDIUM("中太", androidx.compose.ui.text.font.FontWeight.Medium),
    BOLD("太い", androidx.compose.ui.text.font.FontWeight.Bold)
}

/**
 * キャラクタースタイル
 */
enum class CharacterStyle(val displayName: String, val description: String) {
    CUTE("かわいい", "親しみやすいスタイル"),
    COOL("クール", "スタイリッシュなスタイル"),
    SIMPLE("シンプル", "控えめなスタイル")
}

/**
 * キャラクターポジション
 */
enum class CharacterPosition(val displayName: String) {
    TOP_LEFT("左上"),
    TOP_RIGHT("右上"),
    BOTTOM_LEFT("左下"),
    BOTTOM_RIGHT("右下"),
    CENTER("中央")
}

// 残りのセクション実装（Language, Font, Character, Accessibility, Preview）は
// CustomizationScreen.ktの続きとして別途作成予定
