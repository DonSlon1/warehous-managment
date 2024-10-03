package com.example.smartinventory.data.model

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "warehouse_action_items" ,primaryKeys = ["warehouseActionId", "inventoryItemId"])
data class WarehouseActionItem (
    val warehouseActionId: Long = 0L,
    val inventoryItemId: Long = 0L,
    val quantity: Int,
    val price: Double
) : Parcelable
