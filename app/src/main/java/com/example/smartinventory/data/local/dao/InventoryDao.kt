package com.example.smartinventory.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.smartinventory.data.model.InventoryItem

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory_items")
    fun getAllItems(): LiveData<List<InventoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: InventoryItem)

    @Update
    suspend fun update(item: InventoryItem)

    @Delete
    suspend fun delete(item: InventoryItem)
}