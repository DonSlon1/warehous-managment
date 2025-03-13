package com.example.smartinventory.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for handling API data operations
 */
@Singleton
class ApiRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    /**
     * Fetches products from the API
     * @return Result containing either success with a list of products or failure with an exception
     */
    suspend fun getProducts(): Result<List<ApiProduct>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getProducts()
            if (response.isSuccessful) {
                val products = response.body() ?: emptyList()
                Result.success(products)
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}