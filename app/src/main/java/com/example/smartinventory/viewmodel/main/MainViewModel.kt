package com.example.smartinventory.viewmodel.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartinventory.data.local.database.InventoryDatabase
import com.example.smartinventory.data.model.InventoryItem
import com.example.smartinventory.data.repository.InventoryRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InventoryRepository
    
    // Use a mediator LiveData to combine repository data with refresh events
    private val _allInventoryItems = MediatorLiveData<List<InventoryItem>>()
    val allInventoryItems: LiveData<List<InventoryItem>> = _allInventoryItems
    
    // Track when we need to force refresh
    private val refreshTrigger = MutableLiveData<Long>()

    init {
        val inventoryDao = InventoryDatabase.getDatabase(application).inventoryDao()
        repository = InventoryRepository(inventoryDao)
        
        // Add the repository's LiveData as a source
        _allInventoryItems.addSource(repository.allItems) { items ->
            _allInventoryItems.value = items
        }
        
        // Also observe EventBus's inventory updated event
        _allInventoryItems.addSource(com.example.smartinventory.utils.EventBus.inventoryUpdatedEvent) {
            // Force refresh by re-requesting data from repository
            refreshData()
        }
    }
    
    // Method to force refresh the data
    fun refreshData() {
        viewModelScope.launch {
            // Get fresh data directly from the database
            val db = InventoryDatabase.getDatabase(getApplication())
            val freshItems = db.inventoryDao().getAllItemsSync()
            
            // Update the LiveData with fresh data
            _allInventoryItems.postValue(freshItems)
            
            // Also trigger the refresh timestamp
            refreshTrigger.value = System.currentTimeMillis()
        }
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
