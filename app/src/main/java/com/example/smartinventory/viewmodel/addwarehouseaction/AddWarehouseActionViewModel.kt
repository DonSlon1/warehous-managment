package com.example.smartinventory.viewmodel.addwarehouseaction

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.smartinventory.data.model.InventoryItem
import com.example.smartinventory.data.model.WarehouseAction
import com.example.smartinventory.data.model.WarehouseActionItem
import com.example.smartinventory.data.model.WarehouseActionType
import com.example.smartinventory.data.repository.InventoryRepository
import com.example.smartinventory.data.repository.WarehouseItemWithItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddWarehouseActionViewModel @Inject constructor(
    val inventoryRepository: InventoryRepository,
    val repository: WarehouseItemWithItemsRepository
) : ViewModel() {

    var isEditMode: Boolean = false
    var editingWarehouseActionId: Long = -1L
    
    private var _filterQuery = MutableLiveData("")
    val filterQuery: LiveData<String> get() = _filterQuery

    val allInventoryItem: LiveData<List<InventoryItem>> = inventoryRepository.allItems
    val filteredInventoryItem: LiveData<List<InventoryItem>> = _filterQuery.switchMap { query ->
        allInventoryItem.map { actions ->
            if (query.isBlank()) {
                actions
            } else {
                actions.filter {
                    it.name.contains(query, ignoreCase = true) ||
                    it.ean.contains(query, ignoreCase = true)
                }
            }
        }}

    // LiveData for the current warehouse action being edited
    private val _currentWarehouseAction = MutableLiveData<WarehouseAction?>()
    val currentWarehouseAction: LiveData<WarehouseAction?> = _currentWarehouseAction

    // Load a warehouse action for editing
    fun loadWarehouseAction(warehouseActionId: Long) {
        if (warehouseActionId <= 0) {
            isEditMode = false
            _currentWarehouseAction.value = null
            return
        }

        isEditMode = true
        editingWarehouseActionId = warehouseActionId
        
        viewModelScope.launch {
            val actionPair = repository.getWarehouseActionWithItems(warehouseActionId)
            actionPair?.let {
                _currentWarehouseAction.value = it.first
            }
        }
    }

    fun insertWarehouseActionWithItems(warehouseAction: WarehouseAction, items: List<WarehouseActionItem>) {
        viewModelScope.launch {
            try {
                if (isEditMode) {
                    // Editing existing warehouse action
                    repository.updateWarehouseActionWithItems(warehouseAction, items)
                } else {
                    // Creating new warehouse action
                    items.forEach {
                        if (!updateItemQuantity(it, warehouseAction.type)) {
                            throw Exception("Not enough quantity")
                        }
                    }
                    repository.insertWarehouseActionWithItems(warehouseAction, items)
                }
                
                // Force refresh inventory data immediately
                com.example.smartinventory.utils.InventoryRefresher.refreshInventory()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    private suspend fun updateItemQuantity(item: WarehouseActionItem, actionType: WarehouseActionType): Boolean {
        // Get the full inventory item, not just its quantity
        val inventoryItem = inventoryRepository.getItem(item.inventoryItemId) ?: return false
        
        // Calculate new quantity
        var newQuantity = inventoryItem.quantity
        if (actionType == WarehouseActionType.INBOUND) {
            newQuantity += item.quantity
        } else {
            newQuantity -= item.quantity
        }
        
        // Check if quantity would go negative
        if (newQuantity < 0) {
            return false
        }
        
        // Create an updated inventory item
        val updatedItem = inventoryItem.copy(quantity = newQuantity)
        
        // Use update instead of updateQuantity to ensure full entity update
        inventoryRepository.update(updatedItem)
        
        return true
    }

    fun insert(item: WarehouseAction) {
        viewModelScope.launch {
            repository.insert(item)
        }
    }

    fun update(item: WarehouseAction) {
        viewModelScope.launch {
            repository.update(item)
        }
    }

    fun delete(item: WarehouseAction) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }

    fun filterItems(query: String) {
        _filterQuery.value = query
    }

    fun filterItemsByBarcode(barcode: String) {
        _filterQuery.value = barcode
    }

}
