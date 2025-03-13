package com.example.smartinventory.data.api

import com.google.gson.annotations.SerializedName

/**
 * Data class representing a product from the API
 */
data class ApiProduct(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("image")
    val imageUrl: String,
    
    @SerializedName("rating")
    val rating: Rating
)

data class Rating(
    @SerializedName("rate")
    val rate: Double,
    
    @SerializedName("count")
    val count: Int
)