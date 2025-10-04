package com.akira.carrycheck.data.repository

import com.akira.carrycheck.data.dao.ChecklistItemDao
import com.akira.carrycheck.data.entity.ChecklistItemEntity
import com.akira.carrycheck.data.model.ChecklistItem
import com.akira.carrycheck.data.model.Season
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * チェックリストアイテムのリポジトリ
 * データモデルとエンティティの変換、ビジネスロジックを担当
 */
class ItemRepository(
    private val checklistItemDao: ChecklistItemDao
) {

    /**
     * 全てのチェックリストアイテムを取得
     */
    fun getAllItems(): Flow<List<ChecklistItem>> {
        return checklistItemDao.getAllItems().map { entities ->
            entities.map { it.toModel() }
        }
    }

    /**
     * 季節別にチェックリストアイテムを取得
     */
    fun getItemsBySeason(season: Season): Flow<List<ChecklistItem>> {
        return checklistItemDao.getItemsBySeason(season.name).map { entities ->
            entities.map { it.toModel() }
        }
    }

    /**
     * IDでチェックリストアイテムを取得
     */
    suspend fun getItemById(id: Long): ChecklistItem? {
        return checklistItemDao.getItemById(id)?.toModel()
    }

    /**
     * チェックリストアイテムを挿入
     */
    suspend fun insertItem(item: ChecklistItem): Long {
        return checklistItemDao.insertItem(item.toEntity())
    }

    /**
     * 複数のチェックリストアイテムを挿入
     */
    suspend fun insertItems(items: List<ChecklistItem>): List<Long> {
        return checklistItemDao.insertItems(items.map { it.toEntity() })
    }

    /**
     * チェックリストアイテムを更新
     */
    suspend fun updateItem(item: ChecklistItem) {
        checklistItemDao.updateItem(item.toEntity())
    }

    /**
     * チェックリストアイテムを削除
     */
    suspend fun deleteItem(item: ChecklistItem) {
        checklistItemDao.deleteItem(item.toEntity())
    }

    /**
     * IDでチェックリストアイテムを削除
     */
    suspend fun deleteItemById(id: Long) {
        checklistItemDao.deleteItemById(id)
    }

    /**
     * 全てのチェックリストアイテムを削除
     */
    suspend fun deleteAllItems() {
        checklistItemDao.deleteAllItems()
    }
}

/**
 * ChecklistItemEntityからChecklistItemへの変換
 */
private fun ChecklistItemEntity.toModel(): ChecklistItem {
    return ChecklistItem(
        id = this.id,
        name = this.name,
        category = this.category,
        season = Season.valueOf(this.season),
        isChecked = this.isChecked,
        priority = this.priority,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

/**
 * ChecklistItemからChecklistItemEntityへの変換
 */
private fun ChecklistItem.toEntity(): ChecklistItemEntity {
    return ChecklistItemEntity(
        id = this.id,
        name = this.name,
        category = this.category,
        season = this.season.name,
        isChecked = this.isChecked,
        priority = this.priority,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
