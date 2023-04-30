package com.kitclub.kiteventqrscanner.broadcastreceiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kitclub.kiteventqrscanner.activities.MainActivity
import com.kitclub.kiteventqrscanner.utils.DeviceInfo


class InternetConnectionChangeReceiver(private var mainActivity: MainActivity) : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        mainActivity.setConnectionWarning(!DeviceInfo.isNetworkAvailable(context))
    }


}