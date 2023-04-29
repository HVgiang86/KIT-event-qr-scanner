@file:Suppress("DEPRECATION")

package com.example.kiteventqrscanner.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build

object DeviceInfo {
    @SuppressLint("PrivateApi", "HardwareIds")
    fun getSerialNumber(): String? {
        var serialNumber: String?
        try {
            val c = Class.forName("android.os.SystemProperties")
            val get = c.getMethod("get", String::class.java)
            serialNumber = get.invoke(c, "gsm.sn1") as String
            if (serialNumber == "") serialNumber = get.invoke(c, "ril.serialnumber") as String
            if (serialNumber == "") serialNumber = get.invoke(c, "ro.serialno") as String
            if (serialNumber == "") serialNumber = get.invoke(c, "sys.serialnumber") as String
            if (serialNumber == "") serialNumber = Build.SERIAL

            // If none of the methods above worked
            if (serialNumber == "") serialNumber = null
        } catch (e: Exception) {
            e.printStackTrace()
            serialNumber = null
        }
        return serialNumber
    }

    fun isNetworkAvailable(ctx: Context): Boolean {
        val connectivityManager =
            ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}