package com.kitclub.kiteventqrscanner.broadcastreceiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kitclub.kiteventqrscanner.activities.MainActivity
import com.kitclub.kiteventqrscanner.application.AppStatus
import com.kitclub.kiteventqrscanner.utils.DeviceInfo


class InternetConnectionChangeReceiver() : BroadcastReceiver() {
    private lateinit var mainActivity: MainActivity
    constructor(mainActivity: MainActivity) : this(){
        this.mainActivity = mainActivity
    }
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val status = DeviceInfo.isNetworkAvailable(context)
        mainActivity.setConnectionWarning(!status)
        AppStatus.internetConnectionStatus = status
    }


}