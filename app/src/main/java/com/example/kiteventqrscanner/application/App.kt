package com.example.kiteventqrscanner.application

import android.app.Application
import android.util.Log
import com.example.kiteventqrscanner.settings.Settings

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("KIT","App started")
        Settings.getLocalSettings(applicationContext)
    }
}