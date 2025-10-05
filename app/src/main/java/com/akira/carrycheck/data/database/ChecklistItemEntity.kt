package com.akira.carrycheck.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akira.carrycheck.data.model.ChecklistItem
import com.akira.carrycheck.data.model.Season

/**
 * チェックリストアイテムのRoomエンティティ
 * v3.0拡張: 重要度フラグ追加、カテゴリ対応
 */
@Entity(tableName = "checklist_items")
data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // カテゴリ（例：衣類、電子機器、書類等）
    val isChecked: Boolean,
    val season: String, // Season enumを文字列として保存
    val createdAt: Long,
    val updatedAt: Long, // 更新日時
    val isImportant: Boolean = false // 緊急モード用重要フラグ
)

/**
 * EntityからDomainモデルへの変換
 */
fun ChecklistItemEntity.toDomain(): ChecklistItem {
    return ChecklistItem(
        id = id,
        name = name,
        category = category,
        season = Season.valueOf(season),
        isChecked = isChecked,
        priority = 2, // デフォルト中優先度（Entityには保存していない）
        createdAt = createdAt,
        updatedAt = updatedAt,
        isImportant = isImportant
    )
}

/**
 * DomainモデルからEntityへの変換
 */
fun ChecklistItem.toEntity(): ChecklistItemEntity {
    return ChecklistItemEntity(
        id = id,
        name = name,
        category = category,
        isChecked = isChecked,
        season = season.name,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isImportant = isImportant
    )
}
