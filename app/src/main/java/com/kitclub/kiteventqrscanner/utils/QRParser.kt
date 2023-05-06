package com.kitclub.kiteventqrscanner.utils

import android.util.Log
import com.kitclub.kiteventqrscanner.model.models.attendee.Attendee
import com.kitclub.kiteventqrscanner.model.models.settings.Settings
import org.json.JSONObject


object QRParser {
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