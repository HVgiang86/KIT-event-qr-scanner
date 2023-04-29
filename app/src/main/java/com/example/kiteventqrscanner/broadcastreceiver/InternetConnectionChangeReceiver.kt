package com.example.kiteventqrscanner.broadcastreceiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.kiteventqrscanner.activities.MainActivity
import com.example.kiteventqrscanner.utils.DeviceInfo


class InternetConnectionChangeReceiver(private var mainActivity: MainActivity) : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        mainActivity.setConnectionWarning(!DeviceInfo.isNetworkAvailable(context))
    }


}