package com.example.kiteventqrscanner.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.example.kiteventqrscanner.activities.MainActivity
import com.example.kiteventqrscanner.utils.DeviceInfo


class InternetConnectionChangeReceiver(var mainActivity: MainActivity) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
            mainActivity.setConnectionWarning(!DeviceInfo.isNetworkAvailable(context))
    }


}