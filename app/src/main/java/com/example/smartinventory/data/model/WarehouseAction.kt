package com.example.smartinventory.data.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "warehouse_actions")
data class WarehouseAction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val type: String,
    val status: String,
    val name: String,
) : Parcelable

data class WarehouseActionWithItems(
    @Embedded val warehouseAction: WarehouseAction,
    @Relation(
        parentColumn = "id",
        entity = InventoryItem::class,
        entityColumn = "id",
        associateBy = Junction(
            value = WarehouseActionItem::class,
            parentColumn = "warehouseActionId",
            entityColumn = "inventoryItemId"
        )
    )
    val items: List<WarehouseActionItem>
)

enum class WarehouseActionType {
    INBOUND,
    OUTBOUND
}

enum class WarehouseActionStatus {
    DRAFT,
    COMPLETED
}