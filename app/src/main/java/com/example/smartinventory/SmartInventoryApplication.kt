package com.example.smartinventory

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmartInventoryApplication : Application() {
    companion object {
        // Create a singleton instance to access application context
        lateinit var instance: SmartInventoryApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
