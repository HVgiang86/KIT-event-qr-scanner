package com.example.kiteventqrscanner.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.example.kiteventqrscanner.activities.MainActivity


class InternetConnectionChangeReceiver(var mainActivity: MainActivity) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
            mainActivity.setConnectionWarning(!isNetworkAvailable(context))
    }

    private fun isNetworkAvailable(ctx: Context): Boolean {
        val connectivityManager =
            ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}