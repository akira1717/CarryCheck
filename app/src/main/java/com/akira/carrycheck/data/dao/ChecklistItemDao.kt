package com.akira.carrycheck.data.dao

import androidx.room.*
import com.akira.carrycheck.data.entity.ChecklistItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * チェックリストアイテムのDAO（Data Access Object）
 * ChecklistItemEntityに対するCRUD操作を定義
 */
@Dao
interface ChecklistItemDao {

    /**
     * 全てのチェックリストアイテムを取得
     */
    @Query("SELECT * FROM checklist_items ORDER BY priority ASC, created_at DESC")
    fun getAllItems(): Flow<List<ChecklistItemEntity>>

    /**
     * 季節別にチェックリストアイテムを取得
     */
    @Query("SELECT * FROM checklist_items WHERE season = :season ORDER BY priority ASC, created_at DESC")
    fun getItemsBySeason(season: String): Flow<List<ChecklistItemEntity>>

    /**
     * IDでチェックリストアイテムを取得
     */
    @Query("SELECT * FROM checklist_items WHERE id = :id")
    suspend fun getItemById(id: Long): ChecklistItemEntity?

    /**
     * チェックリストアイテムを挿入
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ChecklistItemEntity): Long

    /**
     * 複数のチェックリストアイテムを挿入
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ChecklistItemEntity>): List<Long>

    /**
     * チェックリストアイテムを更新
     */
    @Update
    suspend fun updateItem(item: ChecklistItemEntity)

    /**
     * チェックリストアイテムを削除
     */
    @Delete
    suspend fun deleteItem(item: ChecklistItemEntity)

    /**
     * IDでチェックリストアイテムを削除
     */
    @Query("DELETE FROM checklist_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    /**
     * 全てのチェックリストアイテムを削除
     */
    @Query("DELETE FROM checklist_items")
    suspend fun deleteAllItems()
}
