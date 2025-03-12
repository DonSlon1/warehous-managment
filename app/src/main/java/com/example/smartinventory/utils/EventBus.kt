package com.example.smartinventory.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

// Events that can be posted to the event bus
sealed class AppEvent {
    object InventoryUpdated : AppEvent()
}

// Singleton event bus for application-wide events
object EventBus {
    private val _events = MutableSharedFlow<AppEvent>(replay = 0, extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    // For LiveData compatibility - useful for older code
    private val _inventoryUpdatedEvent = MutableLiveData<Long>()
    val inventoryUpdatedEvent: LiveData<Long> = _inventoryUpdatedEvent

    suspend fun postEvent(event: AppEvent) {
        _events.emit(event)
        if (event is AppEvent.InventoryUpdated) {
            _inventoryUpdatedEvent.postValue(System.currentTimeMillis())
        }
    }
}