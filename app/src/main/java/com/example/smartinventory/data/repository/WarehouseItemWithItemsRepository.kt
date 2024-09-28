package com.example.smartinventory.data.repository

import androidx.lifecycle.LiveData
import com.example.smartinventory.data.local.dao.InventoryDao
import com.example.smartinventory.data.local.dao.WarehouseActionItemWithItemsDao
import com.example.smartinventory.data.model.InventoryItem
import com.example.smartinventory.data.model.WarehouseAction
import com.example.smartinventory.data.model.WarehouseActionWithItems
import javax.inject.Inject

class WarehouseItemWithItemsRepository @Inject constructor(
    warehouseActionItemWithItemsDao: WarehouseActionItemWithItemsDao
) {
    val allItems: LiveData<List<WarehouseAction>> = warehouseActionItemWithItemsDao.getAllItems()
}
