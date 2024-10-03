package com.example.smartinventory.di

import android.content.Context
import androidx.room.Room
import com.example.smartinventory.data.local.dao.InventoryDao
import com.example.smartinventory.data.local.dao.WarehouseActionDao
import com.example.smartinventory.data.local.database.InventoryDatabase
import com.example.smartinventory.data.repository.InventoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext appContext: Context
    ): InventoryDatabase {
        return Room.databaseBuilder(
            appContext,
            InventoryDatabase::class.java,
            "inventory_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Singleton
    @Provides
    fun provideInventoryDao(database: InventoryDatabase): InventoryDao {
        return database.inventoryDao()
    }

    @Singleton
    @Provides
    fun provideWarehouseActionItemWithItemsDao(database: InventoryDatabase): WarehouseActionDao {
        return database.warehouseActionItemWithItemsDao()
    }

    @Singleton
    @Provides
    fun provideInventoryRepository(dao: InventoryDao): InventoryRepository {
        return InventoryRepository(dao)
    }
}