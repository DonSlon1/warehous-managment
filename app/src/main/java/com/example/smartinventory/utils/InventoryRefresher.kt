package com.example.smartinventory.utils

import com.example.smartinventory.SmartInventoryApplication
import com.example.smartinventory.data.local.database.InventoryDatabase
import com.example.smartinventory.viewmodel.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Utility class to refresh inventory data across the application
 */
object InventoryRefresher {
    
    private var mainViewModel: MainViewModel? = null
    
    /**
     * Register the MainViewModel to allow refreshing it from anywhere in the app
     */
    fun registerMainViewModel(viewModel: MainViewModel) {
        mainViewModel = viewModel
    }
    
    /**
     * Unregister the MainViewModel (to prevent memory leaks)
     */
    fun unregisterMainViewModel() {
        mainViewModel = null
    }
    
    /**
     * Force refresh the inventory data immediately
     * This can be called from anywhere in the app after a warehouse action is completed
     */
    fun refreshInventory() {
        // First try to refresh through the MainViewModel if available
        mainViewModel?.refreshData()
        
        // As a fallback, also try to update the database to trigger LiveData
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Access the database directly as a fallback
                val db = InventoryDatabase.getDatabase(SmartInventoryApplication.instance)
                // Just query the data to ensure it's fresh
                db.inventoryDao().getAllItemsSync()
            } catch (e: Exception) {
                // Log any errors but don't crash
                e.printStackTrace()
            }
        }
    }
}