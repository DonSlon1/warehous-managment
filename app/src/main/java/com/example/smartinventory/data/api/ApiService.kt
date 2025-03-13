package com.example.smartinventory.data.api

import retrofit2.Response
import retrofit2.http.GET

/**
 * Retrofit interface for API calls
 */
interface ApiService {
    
    /**
     * Fetches a list of products from the fakestoreapi.com
     */
    @GET("products")
    suspend fun getProducts(): Response<List<ApiProduct>>
}