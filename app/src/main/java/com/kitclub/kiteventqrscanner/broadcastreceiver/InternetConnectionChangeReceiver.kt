package com.kitclub.kiteventqrscanner.broadcastreceiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kitclub.kiteventqrscanner.application.AppStatus
import com.kitclub.kiteventqrscanner.utils.DeviceInfo
import kotlin.reflect.full.functions

class InternetConnectionChangeReceiver<T>() : BroadcastReceiver() {
    private var activity: T? = null

    constructor(activity: T) : this() {
        this.activity = activity
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        setWarning(context)
    }

    private fun setWarning(context: Context) {
        val status = DeviceInfo.isNetworkAvailable(context)

        AppStatus.internetConnectionStatus = status
        val m = activity!!::class.functions.find { it.name == "setConnectionWarning" }

        m?.call(activity,!status)
        //activity.setConnectionWarning(!status)
    }


}