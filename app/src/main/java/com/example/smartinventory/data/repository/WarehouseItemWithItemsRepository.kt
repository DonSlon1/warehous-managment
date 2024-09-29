package com.example.smartinventory.data.repository

import androidx.lifecycle.LiveData
import com.example.smartinventory.data.local.dao.WarehouseActionItemWithItemsDao
import com.example.smartinventory.data.model.WarehouseAction
import javax.inject.Inject

class WarehouseItemWithItemsRepository @Inject constructor(
    private val warehouseActionItemWithItemsDao: WarehouseActionItemWithItemsDao
) {
    val allItems: LiveData<List<WarehouseAction>> = warehouseActionItemWithItemsDao.getAllItems()

    suspend fun insert(item: WarehouseAction) {
        warehouseActionItemWithItemsDao.insert(item)
    }

    suspend fun update(item: WarehouseAction) {
        warehouseActionItemWithItemsDao.update(item)
    }

    suspend fun delete(item: WarehouseAction) {
        warehouseActionItemWithItemsDao.delete(item)
    }
}
