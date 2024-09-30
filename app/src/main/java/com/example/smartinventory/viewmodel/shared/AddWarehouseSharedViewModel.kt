package com.example.smartinventory.viewmodel.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.smartinventory.data.model.InventoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddWarehouseSharedViewModel @Inject constructor() : ViewModel() {
    // Selected items from FilterInventoryItems
    private val _selectedItems = MutableLiveData<List<InventoryItem>>()
    val selectedItems: LiveData<List<InventoryItem>> get() = _selectedItems

    fun setSelectedItems(items: List<InventoryItem>) {
        _selectedItems.value = items
    }
}
