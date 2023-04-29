package com.example.kiteventqrscanner.qrhelper

import android.util.Log
import com.example.kiteventqrscanner.model.Attendee
import com.example.kiteventqrscanner.settings.Settings
import com.example.kiteventqrscanner.utils.DeviceInfo
import com.example.kiteventqrscanner.utils.MD5
import org.json.JSONObject


object QRHelper {
    fun getAttendee(content: String): Attendee? {
        return try {
            val id =
                "KIT" + MD5.hash("" + System.currentTimeMillis() + DeviceInfo.getSerialNumber())
            val root = JSONObject(content)
            val attendeeParamList: HashMap<String, String> = HashMap()
            for (param in Settings.paramList) {
                val paramValue = root.getString(param.name)
                attendeeParamList[param.name] = paramValue
                Log.d("KIT", "${param.name}: $paramValue")
            }
            Attendee(id, attendeeParamList)

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("KIT", "NULL")
            null
        }
    }


}