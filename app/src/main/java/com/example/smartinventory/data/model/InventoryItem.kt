package com.example.smartinventory.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey val id: String,
    val name: String,
    val sku: String,
    val category: String,
    val quantity: Int,
    val supplier: String,
    val reorderLevel: Int,
    val unitPrice: Double,
    val expiryDate: Date,
    val imageUrl: String?
)