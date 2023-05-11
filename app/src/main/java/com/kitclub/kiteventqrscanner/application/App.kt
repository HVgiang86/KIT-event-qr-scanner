package com.kitclub.kiteventqrscanner.application

import android.app.Application
import android.util.Log
import com.kitclub.kiteventqrscanner.controller.QRScanController
import com.kitclub.kiteventqrscanner.model.firebase.FirebaseHelper
import com.kitclub.kiteventqrscanner.model.models.settings.Settings
import com.kitclub.kiteventqrscanner.model.repository.RealmHelper
import com.kitclub.kiteventqrscanner.model.repository.SettingsReferences
import com.kitclub.kiteventqrscanner.utils.AESHelper

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("KIT","App started ${System.currentTimeMillis()}")
        SettingsReferences.getLocalSettings(applicationContext)
        
    }

    override fun onTerminate() {
        RealmHelper.close()
        Log.d("KIT","app terminated")
        super.onTerminate()
    }

}