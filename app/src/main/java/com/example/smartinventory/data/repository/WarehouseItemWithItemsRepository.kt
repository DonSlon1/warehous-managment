package com.example.smartinventory.data.repository

import androidx.lifecycle.LiveData
import com.example.smartinventory.data.local.dao.WarehouseActionDao
import com.example.smartinventory.data.model.WarehouseAction
import com.example.smartinventory.data.model.WarehouseActionItem
import javax.inject.Inject

class WarehouseItemWithItemsRepository @Inject constructor(
    private val warehouseActionItemWithItemsDao: WarehouseActionDao
) {
    val allItems: LiveData<List<WarehouseAction>> = warehouseActionItemWithItemsDao.getAllItems()

    suspend fun insert(item: WarehouseAction) {
        warehouseActionItemWithItemsDao.insert(item)
    }

    suspend fun insertWarehouseActionWithItems(
        warehouseAction: WarehouseAction,
        items: List<WarehouseActionItem>
    ) {
        warehouseActionItemWithItemsDao.insertWarehouseActionWithItems(warehouseAction, items)
    }

    suspend fun updateWarehouseActionWithItems(
        warehouseAction: WarehouseAction,
        items: List<WarehouseActionItem>
    ) {
        warehouseActionItemWithItemsDao.updateWarehouseActionWithItems(warehouseAction, items)
    }

    suspend fun getWarehouseActionWithItems(warehouseActionId: Long): Pair<WarehouseAction, List<WarehouseActionItem>>? {
        return warehouseActionItemWithItemsDao.getWarehouseActionWithItems(warehouseActionId)
    }

    suspend fun update(item: WarehouseAction) {
        warehouseActionItemWithItemsDao.update(item)
    }

    suspend fun delete(item: WarehouseAction) {
        warehouseActionItemWithItemsDao.delete(item)
    }
}
