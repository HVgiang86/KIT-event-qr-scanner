package com.example.kiteventqrscanner.qrhelper

import com.example.kiteventqrscanner.model.Attendee
import com.example.kiteventqrscanner.utils.MD5
import org.json.JSONException
import org.json.JSONObject

object QRHelper {
    fun getAttendee(content: String): Attendee? {
        return try {
            val root = JSONObject(content)
            val emailStr = "" + root.getString("email")
            val codeStr = "" + root.getString("code")
            val id = "KIT" + MD5.hash("" + System.currentTimeMillis() + emailStr)

            Attendee(id, emailStr, codeStr)
        } catch (e: JSONException) {
            e.printStackTrace()
            null
        }
    }
}