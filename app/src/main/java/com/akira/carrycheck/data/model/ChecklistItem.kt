package com.akira.carrycheck.data.model

/**
 * チェックリストアイテムのデータモデル
 *
 * @property id アイテムの一意識別子
 * @property name アイテム名
 * @property category カテゴリ（例：衣類、電子機器、書類等）
 * @property season 使用季節
 * @property isChecked チェック状態
 * @property priority 優先度（1:高、2:中、3:低）
 * @property createdAt 作成日時
 * @property updatedAt 更新日時
 */
data class ChecklistItem(
    val id: Long = 0L,
    val name: String,
    val category: String,
    val season: Season,
    val isChecked: Boolean = false,
    val priority: Int = 2, // デフォルトは中優先度
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
