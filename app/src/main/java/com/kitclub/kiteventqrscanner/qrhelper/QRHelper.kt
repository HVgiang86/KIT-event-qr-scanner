package com.kitclub.kiteventqrscanner.qrhelper

import android.util.Log
import com.kitclub.kiteventqrscanner.model.Attendee
import com.kitclub.kiteventqrscanner.settings.Settings
import com.kitclub.kiteventqrscanner.utils.DeviceInfo
import com.kitclub.kiteventqrscanner.utils.MD5
import org.json.JSONObject


object QRHelper {
    fun getAttendee(content: String): Attendee? {
        return try {
            val id =
                "KIT" + MD5.hash("" + System.currentTimeMillis() + DeviceInfo.getSerialNumber())
            val root = JSONObject(content)
            val attendeeParamList: HashMap<String, String> = HashMap()
            for (param in Settings.paramList) {
                if (param.required) {
                    val paramValue = root.getString(param.name)
                    attendeeParamList[param.name] = paramValue
                    Log.d("KIT", "${param.name}: $paramValue")
                }
            }

            for (param in Settings.paramList) {
                if (!param.required) {
                    try {
                        val paramValue = root.getString(param.name)
                        attendeeParamList[param.name] = paramValue
                        Log.d("KIT", "${param.name}: $paramValue")
                    } catch (e: Exception) {
                        attendeeParamList[param.name] = ""
                        continue
                    }
                }
            }
            Attendee(id, attendeeParamList)

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("KIT", "NULL")
            null
        }
    }


}