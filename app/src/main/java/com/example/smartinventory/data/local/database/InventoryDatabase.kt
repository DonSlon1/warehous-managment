package com.example.smartinventory.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.test.espresso.core.internal.deps.dagger.Provides
import com.example.smartinventory.data.local.dao.InventoryDao
import com.example.smartinventory.data.local.dao.WarehouseActionItemWithItemsDao
import com.example.smartinventory.data.model.InventoryItem
import com.example.smartinventory.data.model.WarehouseAction
import com.example.smartinventory.utils.Converters

@Database(
    entities = [
        InventoryItem::class,
        WarehouseAction::class
    ], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class InventoryDatabase : RoomDatabase() {

    @Provides
    abstract fun inventoryDao(): InventoryDao
    @Provides
    abstract fun warehouseActionItemWithItemsDao(): WarehouseActionItemWithItemsDao

    companion object {
        @Volatile private var INSTANCE: InventoryDatabase? = null

        fun getDatabase(context: Context): InventoryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InventoryDatabase::class.java,
                    "inventory_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
