package com.akira.carrycheck.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akira.carrycheck.data.model.ChecklistItem
import com.akira.carrycheck.data.model.Season

/**
 * チェックリストアイテムのRoomエンティティ
 * v3.0拡張: 重要度フラグ追加
 */
@Entity(tableName = "checklist_items")
data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val isChecked: Boolean,
    val season: String, // Season enumを文字列として保存
    val createdAt: Long,
    val isImportant: Boolean = false  // 緊急モード用重要フラグ
)

/**
 * EntityからDomainモデルへの変換
 */
fun ChecklistItemEntity.toDomain(): ChecklistItem {
    return ChecklistItem(
        id = id,
        name = name,
        isChecked = isChecked,
        season = Season.valueOf(season),
        createdAt = createdAt,
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
        isChecked = isChecked,
        season = season.name,
        createdAt = createdAt,
        isImportant = isImportant
    )
}
