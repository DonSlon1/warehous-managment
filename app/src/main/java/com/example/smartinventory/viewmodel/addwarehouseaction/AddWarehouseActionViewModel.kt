package com.example.smartinventory.viewmodel.addwarehouseaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartinventory.data.model.WarehouseAction
import com.example.smartinventory.data.repository.WarehouseItemWithItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.MutableState
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.example.smartinventory.data.model.InventoryItem
import com.example.smartinventory.data.repository.InventoryRepository
import com.example.smartinventory.databinding.FragmentAddWarehouseItemBinding
import com.example.smartinventory.viewmodel.addwarehouseaction.AddWarehouseActionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.switchMap
import javax.inject.Inject

@HiltViewModel
class AddWarehouseActionViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository,
    private val repository: WarehouseItemWithItemsRepository
) : ViewModel() {

    var isEditMode: Boolean = false
    private var _filterQuery = MutableLiveData("")
    val filterQuery: LiveData<String> get() = _filterQuery

    val allInventoryItem: LiveData<List<InventoryItem>> = inventoryRepository.allItems
    val filteredInventoryItem: LiveData<List<InventoryItem>> = _filterQuery.switchMap { query ->
        allInventoryItem.map { actions ->
            if (query.isBlank()) {
                actions
            } else {
                actions.filter { it.name.contains(query, ignoreCase = true) }
            }
        }}


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
