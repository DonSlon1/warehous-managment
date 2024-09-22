package com.example.smartinventory.data.repository

import androidx.lifecycle.LiveData
import com.example.smartinventory.data.local.dao.InventoryDao
import com.example.smartinventory.data.model.InventoryItem
import javax.inject.Inject

class InventoryRepository @Inject constructor(
    private val inventoryDao: InventoryDao
) {
    val allItems: LiveData<List<InventoryItem>> = inventoryDao.getAllItems()

    suspend fun insert(item: InventoryItem) {
        inventoryDao.insert(item)
    }

    suspend fun update(item: InventoryItem) {
        inventoryDao.update(item)
    }

    suspend fun delete(item: InventoryItem) {
        inventoryDao.delete(item)
    }
}
