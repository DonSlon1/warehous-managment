package com.example.smartinventory.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.smartinventory.data.model.WarehouseAction

@Dao
interface WarehouseActionItemWithItemsDao {
    @Query("SELECT * FROM warehouse_actions")
    fun getAllItems(): LiveData<List<WarehouseAction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(warehouseAction: WarehouseAction)

    @Update
    suspend fun update(warehouseAction: WarehouseAction)

    @Delete
    suspend fun delete(warehouseAction: WarehouseAction)
}