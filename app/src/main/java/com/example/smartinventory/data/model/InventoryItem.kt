package com.example.smartinventory.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
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