package com.example.smartinventory.viewmodel.warehouseaction


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.smartinventory.data.model.WarehouseAction
import com.example.smartinventory.data.repository.WarehouseItemWithItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WarehouseActionViewModel @Inject constructor(
    private val repository: WarehouseItemWithItemsRepository
) : ViewModel() {
    val allActionItems: LiveData<List<WarehouseAction>> = repository.allItems
}