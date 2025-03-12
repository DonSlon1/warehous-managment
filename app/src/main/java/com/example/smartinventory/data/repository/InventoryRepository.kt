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

    // Removed redundant method as we already have the allItems property
    // that provides the same LiveData

    suspend fun updateQuantity(id: Long, quantity: Int) {
        // Use the new method that forces invalidation
        inventoryDao.updateQuantityWithInvalidation(id, quantity)
    }
    suspend fun getQuantity(id: Long): Int {
        return inventoryDao.getQuantity(id) ?: 0
    }
}
