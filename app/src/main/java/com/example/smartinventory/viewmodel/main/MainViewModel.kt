package com.example.smartinventory.viewmodel.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.smartinventory.data.local.database.InventoryDatabase
import com.example.smartinventory.data.model.InventoryItem
import com.example.smartinventory.data.repository.InventoryRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InventoryRepository
    val allInventoryItems: LiveData<List<InventoryItem>>

    init {
        val inventoryDao = InventoryDatabase.getDatabase(application).inventoryDao()
        repository = InventoryRepository(inventoryDao)
        allInventoryItems = repository.allItems
    }

    fun insert(item: InventoryItem) = viewModelScope.launch {
        repository.insert(item)
    }

    fun update(item: InventoryItem) = viewModelScope.launch {
        repository.update(item)
    }

    fun delete(item: InventoryItem) = viewModelScope.launch {
        repository.delete(item)
    }
}
