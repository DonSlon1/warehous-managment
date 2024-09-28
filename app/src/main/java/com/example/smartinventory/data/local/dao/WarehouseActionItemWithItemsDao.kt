package com.example.smartinventory.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.smartinventory.data.model.WarehouseAction

@Dao
interface WarehouseActionItemWithItemsDao {
    @Query("SELECT * FROM warehouse_actions")
    fun getAllItems(): LiveData<List<WarehouseAction>>
}