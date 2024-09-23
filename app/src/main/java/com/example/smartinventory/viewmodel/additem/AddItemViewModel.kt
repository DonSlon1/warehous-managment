package com.example.smartinventory.viewmodel.additem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartinventory.data.model.InventoryItem
import com.example.smartinventory.data.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    fun insert(item: InventoryItem) {
        viewModelScope.launch {
            repository.insert(item)
        }
    }

    fun update(item: InventoryItem) {
        viewModelScope.launch {
            repository.update(item)
        }
    }
}
