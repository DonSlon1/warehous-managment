package com.example.smartinventory.viewmodel.apidata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartinventory.data.api.ApiProduct
import com.example.smartinventory.data.api.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApiDataViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    // UI states for loading, success, and error
    private val _uiState = MutableLiveData<ApiDataUiState>(ApiDataUiState.Loading)
    val uiState: LiveData<ApiDataUiState> = _uiState

    init {
        loadProducts()
    }

    /**
     * Loads products from the API
     */
    fun loadProducts() {
        _uiState.value = ApiDataUiState.Loading
        viewModelScope.launch {
            repository.getProducts()
                .onSuccess { products ->
                    _uiState.value = ApiDataUiState.Success(products)
                }
                .onFailure { error ->
                    _uiState.value = ApiDataUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    /**
     * Refreshes products from the API
     */
    fun refreshProducts() {
        loadProducts()
    }
}

/**
 * Sealed interface representing different UI states for the API data page
 */
sealed interface ApiDataUiState {
    /**
     * Loading state while waiting for API response
     */
    object Loading : ApiDataUiState

    /**
     * Success state with products data
     */
    data class Success(val products: List<ApiProduct>) : ApiDataUiState

    /**
     * Error state with error message
     */
    data class Error(val message: String) : ApiDataUiState
}