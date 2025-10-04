package com.akira.carrycheck.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.akira.carrycheck.data.model.Season

/**
 * チェックリストアイテムのRoomエンティティ
 * データベーステーブル：checklist_items
 */
@Entity(tableName = "checklist_items")
data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "season")
    val season: String, // SeasonをStringとして保存

    @ColumnInfo(name = "is_checked")
    val isChecked: Boolean = false,

    @ColumnInfo(name = "priority")
    val priority: Int = 2,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
