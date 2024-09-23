package com.example.smartinventory.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey val id: String,
    val name: String,
    val sku: String,
    val ean: String,
    val category: String,
    val quantity: Int,
    val supplier: String,
    val reorderLevel: Int,
    val unitPrice: Double,
    val expiryDate: Date,
    val imageUrl: String?
) : Parcelable