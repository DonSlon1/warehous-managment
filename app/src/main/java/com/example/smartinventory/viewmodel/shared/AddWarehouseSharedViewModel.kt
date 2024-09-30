package com.example.smartinventory.viewmodel.shared

import androidx.lifecycle.ViewModel
import com.example.smartinventory.data.model.InventoryItem
import com.example.smartinventory.ui.addwarehouseitem.view.NewWarehouseItem
import com.example.smartinventory.ui.addwarehouseitem.view.WarehouseActionType
import com.example.smartinventory.ui.addwarehouseitem.view.WarehouseActionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class AddWarehouseSharedViewModel @Inject constructor() : ViewModel() {

    // **Warehouse Action Details**
    private val _actionName = MutableStateFlow("")
    val actionName: StateFlow<String> get() = _actionName

    private val _actionType = MutableStateFlow(WarehouseActionType.INBOUND)
    val actionType: StateFlow<WarehouseActionType> get() = _actionType

    private val _actionStatus = MutableStateFlow(WarehouseActionStatus.DRAFT)
    val actionStatus: StateFlow<WarehouseActionStatus> get() = _actionStatus

    // **Added Items**
    private val _addedItems = MutableStateFlow(mutableListOf<NewWarehouseItem>())
    val addedItems: StateFlow<MutableList<NewWarehouseItem>> get() = _addedItems

    // **Selected Inventory Items**
    private val _selectedItem: MutableStateFlow<InventoryItem?> = MutableStateFlow(null)

    val selectedItem: StateFlow<InventoryItem?> get() = _selectedItem

    // **Updating Action Details**
    fun setActionName(name: String) {
        _actionName.value = name
    }

    fun setActionType(type: WarehouseActionType) {
        _actionType.value = type
    }

    fun setActionStatus(status: WarehouseActionStatus) {
        _actionStatus.value = status
    }

    // **Handling Items**
    fun addItem(item: NewWarehouseItem) {
        val currentItems = _addedItems.value.toMutableList()
        currentItems.add(item)
        _addedItems.value = currentItems
    }

    fun updateItem(updatedItem: NewWarehouseItem) {
        val currentItems = _addedItems.value.toMutableList()
        val index = currentItems.indexOfFirst { it.id == updatedItem.id }
        if (index != -1) {
            currentItems[index] = updatedItem
        }
        _addedItems.value = currentItems
    }

    fun removeItem(item: NewWarehouseItem) {
        val currentItems = _addedItems.value.toMutableList()
        currentItems.remove(item)
        _addedItems.value = currentItems
    }

    // **Selection Handling**
    fun setSelectedItem(items: InventoryItem) {
        _selectedItem.value = items
    }
}
