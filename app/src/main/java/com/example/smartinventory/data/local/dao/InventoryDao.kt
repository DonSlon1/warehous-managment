package com.example.smartinventory.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.smartinventory.data.model.InventoryItem

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory_items")
    fun getAllItems(): LiveData<List<InventoryItem>>

    @Query("SELECT * FROM inventory_items WHERE id = :id")
    suspend fun getItem(id: Long): InventoryItem?

    @Query("SELECT quantity FROM inventory_items WHERE id = :id")
    suspend fun getQuantity(id: Long): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: InventoryItem)

    @Query("UPDATE inventory_items SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: Long, quantity: Int)

    @Update
    suspend fun update(item: InventoryItem)

    @Delete
    suspend fun delete(item: InventoryItem)
}