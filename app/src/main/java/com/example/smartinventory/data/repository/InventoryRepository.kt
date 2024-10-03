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

    suspend fun getItem(id: Long): InventoryItem? {
        return inventoryDao.getItem(id)
    }

    suspend fun getAllItems(): LiveData<List<InventoryItem>> {
        return inventoryDao.getAllItems()
    }

    suspend fun updateQuantity(id: Long, quantity: Int) {
        inventoryDao.updateQuantity(id, quantity)
    }
    suspend fun getQuantity(id: Long): Int {
        return inventoryDao.getQuantity(id) ?: 0
    }
}
