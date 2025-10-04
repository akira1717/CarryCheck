package com.akira.carrycheck.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * チェックリストアイテムのDAO
 * v3.0拡張: 重要度検索、季節フィルター対応
 */
@Dao
interface ChecklistItemDao {

    /**
     * 全アイテム取得（作成日時降順）
     */
    @Query("SELECT * FROM checklist_items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<ChecklistItemEntity>>

    /**
     * IDでアイテム取得
     */
    @Query("SELECT * FROM checklist_items WHERE id = :id")
    suspend fun getItemById(id: Long): ChecklistItemEntity?

    /**
     * 重要アイテムのみ取得
     */
    @Query("SELECT * FROM checklist_items WHERE isImportant = 1 ORDER BY createdAt DESC")
    fun getImportantItems(): Flow<List<ChecklistItemEntity>>

    /**
     * 季節別アイテム取得
     */
    @Query("SELECT * FROM checklist_items WHERE season = :season ORDER BY createdAt DESC")
    fun getItemsBySeason(season: String): Flow<List<ChecklistItemEntity>>

    /**
     * 未チェックアイテム取得
     */
    @Query("SELECT * FROM checklist_items WHERE isChecked = 0 ORDER BY createdAt DESC")
    fun getUncheckedItems(): Flow<List<ChecklistItemEntity>>

    /**
     * 緊急モード用アイテム取得（重要 or 最近追加）
     */
    @Query("""
        SELECT * FROM checklist_items 
        WHERE isImportant = 1 OR createdAt > :since 
        ORDER BY isImportant DESC, createdAt DESC
    """)
    fun getEmergencyItems(since: Long): Flow<List<ChecklistItemEntity>>

    /**
     * 名前で検索
     */
    @Query("SELECT * FROM checklist_items WHERE name LIKE :query ORDER BY createdAt DESC")
    fun searchItems(query: String): Flow<List<ChecklistItemEntity>>

    /**
     * アイテム挿入
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ChecklistItemEntity): Long

    /**
     * 複数アイテム挿入
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ChecklistItemEntity>)

    /**
     * アイテム更新
     */
    @Update
    suspend fun updateItem(item: ChecklistItemEntity)

    /**
     * アイテム削除
     */
    @Delete
    suspend fun deleteItem(item: ChecklistItemEntity)

    /**
     * IDでアイテム削除
     */
    @Query("DELETE FROM checklist_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    /**
     * 全アイテム削除
     */
    @Query("DELETE FROM checklist_items")
    suspend fun deleteAllItems()

    /**
     * チェック済みアイテム削除
     */
    @Query("DELETE FROM checklist_items WHERE isChecked = 1")
    suspend fun deleteCheckedItems()

    /**
     * 古いアイテム削除（指定日数より古い）
     */
    @Query("DELETE FROM checklist_items WHERE createdAt < :before")
    suspend fun deleteOldItems(before: Long)

    /**
     * アイテム数取得
     */
    @Query("SELECT COUNT(*) FROM checklist_items")
    suspend fun getItemCount(): Int

    /**
     * 重要アイテム数取得
     */
    @Query("SELECT COUNT(*) FROM checklist_items WHERE isImportant = 1")
    suspend fun getImportantItemCount(): Int
}
