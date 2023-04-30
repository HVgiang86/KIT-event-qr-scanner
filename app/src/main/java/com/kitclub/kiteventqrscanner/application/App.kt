package com.kitclub.kiteventqrscanner.application

import android.app.Application
import android.util.Log
import com.kitclub.kiteventqrscanner.database.RealmHelper
import com.kitclub.kiteventqrscanner.settings.Settings

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("KIT","App started ${System.currentTimeMillis()}")
        Settings.getLocalSettings(applicationContext)
    }

    override fun onTerminate() {

        RealmHelper.close()
        super.onTerminate()
    }
}