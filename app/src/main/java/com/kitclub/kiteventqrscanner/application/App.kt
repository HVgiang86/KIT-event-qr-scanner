package com.kitclub.kiteventqrscanner.application

import android.app.Application
import android.util.Log
import com.kitclub.kiteventqrscanner.model.repository.SettingsReferences

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("KIT","App started ${System.currentTimeMillis()}")
        SettingsReferences.getLocalSettings(applicationContext)
        
    }

    override fun onTerminate() {
        Log.d("KIT","app terminated")
        super.onTerminate()
    }

}