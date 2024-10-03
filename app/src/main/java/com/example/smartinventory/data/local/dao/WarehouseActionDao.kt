package com.example.smartinventory.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.smartinventory.data.model.WarehouseAction
import com.example.smartinventory.data.model.WarehouseActionItem

@Dao
interface WarehouseActionDao {
    @Query("SELECT * FROM warehouse_actions")
    fun getAllItems(): LiveData<List<WarehouseAction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(warehouseAction: WarehouseAction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWarehouseAction(warehouseAction: WarehouseAction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWarehouseActionItem(warehouseActionItem: WarehouseActionItem)

    @Transaction
    suspend fun insertWarehouseActionWithItems(
        warehouseAction: WarehouseAction,
        items: List<WarehouseActionItem>
    ) {
        val warehouseActionId = insertWarehouseAction(warehouseAction)
        items.forEach { item ->
            insertWarehouseActionItem(item.copy(warehouseActionId = warehouseActionId))
        }
    }

    @Update
    suspend fun update(warehouseAction: WarehouseAction)

    @Delete
    suspend fun delete(warehouseAction: WarehouseAction)
}